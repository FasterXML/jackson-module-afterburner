package com.fasterxml.jackson.module.afterburner.ser;

import java.util.*;

import org.codehaus.jackson.map.ser.BeanPropertyWriter;

import org.codehaus.jackson.org.objectweb.asm.ClassWriter;
import org.codehaus.jackson.org.objectweb.asm.Label;
import org.codehaus.jackson.org.objectweb.asm.MethodVisitor;

import static org.codehaus.jackson.org.objectweb.asm.Opcodes.*;

import com.fasterxml.jackson.module.afterburner.util.MyClassLoader;

/**
 * Simple collector used to keep track of properties for which code-generated
 * accessors are needed.
 */
public class PropertyCollector
{
    private final ArrayList<IntMethodPropertyWriter> _intGetters = new ArrayList<IntMethodPropertyWriter>();
    
    public PropertyCollector() { }

    /*
    /**********************************************************
    /* Methods for collecting properties
    /**********************************************************
     */
    
    public IntMethodPropertyWriter addIntGetter(BeanPropertyWriter bpw) {
        return _add(_intGetters, new IntMethodPropertyWriter(bpw, null, _intGetters.size(), null));
    }

    /*
    /**********************************************************
    /* Code generation; high level
    /**********************************************************
     */

    public BeanPropertyAccessor findAccessor(Class<?> beanType)
    {
        String srcName = beanType.getName() + "$Access4JacksonSerializer";
        
        String generatedClass = internalClassName(srcName);
        MyClassLoader classLoader = new MyClassLoader(beanType.getClassLoader());
        Class<?> accessorClass = null;
        try {
            accessorClass = classLoader.loadClass(srcName);
        } catch (ClassNotFoundException e) { }
        if (accessorClass == null) {
            accessorClass = generateAccessorClass(beanType, classLoader, srcName, generatedClass);
        }
        try {
            return (BeanPropertyAccessor) accessorClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate accessor class '"+srcName+"': "+e.getMessage(), e);
        }
    }
        
    public Class<?> generateAccessorClass(Class<?> beanType,
            MyClassLoader classLoader, String srcName, String generatedClass)
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        String superClass = internalClassName(BeanPropertyAccessor.class.getName());
        
        // muchos important: level at least 1.5 to get generics!!!
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, generatedClass, null, superClass, null);
        cw.visitSource(srcName + ".java", null);
        generateDefaultConstructor(cw, superClass);
        final String beanClass = internalClassName(beanType.getName());
        
        if (!_intGetters.isEmpty()) {
            _addIntGetters(cw, _intGetters, beanClass);
        }
        cw.visitEnd();
        byte[] byteCode = cw.toByteArray();
        return classLoader.loadAndResolve(srcName, byteCode);
    }

    /*
    /**********************************************************
    /* Code generation; int getters
    /**********************************************************
     */
    
    private static void _addIntGetters(ClassWriter cw, List<IntMethodPropertyWriter> props,
            String beanClass)
    {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "intGetter", "(Ljava/lang/Object;I)I", /*generic sig*/null, null);
        mv.visitCode();
        // first: cast bean to proper type
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, beanClass);
        mv.visitVarInsn(ASTORE, 3);

        // Ok; minor optimization, 4 or less accessors, just do IFs; over that, use switch
        if (props.size() <= 4) {
            _addIntGettersIf(mv, props, beanClass);
        } else {
            _addIntGettersSwitch(mv, props, beanClass);
        }

        // and if no match, generate exception:
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V");
        mv.visitInsn(ATHROW);

        // and that's it
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();
    }

    private final static int[] ALL_ICONSTS = new int[] {
        ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4
    };
    
    private static void _addIntGettersIf(MethodVisitor mv, List<IntMethodPropertyWriter> props,
            String beanClass)
    {
        mv.visitVarInsn(ILOAD, 2); // load second arg (index)
        Label next = new Label();
        // first: check if 'index == 0'
        mv.visitJumpInsn(IFNE, next); // "if not zero, goto L (skip stuff)"

        // call first getter:
        mv.visitVarInsn(ALOAD, 3); // load local for cast bean
        mv.visitMethodInsn(INVOKEVIRTUAL, beanClass, props.get(0).getMember().getName(), "()I");
        mv.visitInsn(IRETURN);

        // And from this point on, loop a bit
        for (int i = 1, len = props.size(); i < len; ++i) {
            mv.visitLabel(next);
            next = new Label();
            mv.visitVarInsn(ILOAD, 2); // load second arg (index)
            mv.visitInsn(ALL_ICONSTS[i]);
            mv.visitJumpInsn(IF_ICMPNE, next);
            mv.visitVarInsn(ALOAD, 3); // load bean
            mv.visitMethodInsn(INVOKEVIRTUAL, beanClass, props.get(i).getMember().getName(), "()I");
            mv.visitInsn(IRETURN);
        }
        // and if no match, throw error
        mv.visitLabel(next);
    }        

    private static void _addIntGettersSwitch(MethodVisitor mv, List<IntMethodPropertyWriter> props,
            String beanClass)
    {
        mv.visitVarInsn(ILOAD, 2); // load second arg (index)

        Label[] labels = new Label[props.size()];
        for (int i = 0, len = labels.length; i < len; ++i) {
            labels[i] = new Label();
        }
        Label defaultLabel = new Label();
        mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
        for (int i = 0, len = labels.length; i < len; ++i) {
            mv.visitLabel(labels[i]);
            mv.visitVarInsn(ALOAD, 3); // load bean
            mv.visitMethodInsn(INVOKEVIRTUAL, beanClass, props.get(i).getMember().getName(), "()I");
            mv.visitInsn(IRETURN);
        }
        mv.visitLabel(defaultLabel);
    }        
    
    private static void generateDefaultConstructor(ClassWriter cw, String superName)
    {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();
    }
    
    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private static String internalClassName(String className) {
        return className.replace(".", "/");
    }
    
    private <T extends OptimizedBeanPropertyWriter<T>> T _add(List<T> list, T value) {
        list.add(value);
        return value;
    }
}
