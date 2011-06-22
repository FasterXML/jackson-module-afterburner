package com.fasterxml.jackson.module.afterburner.ser;

import java.lang.reflect.Method;
import java.util.*;

import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;

import org.codehaus.jackson.org.objectweb.asm.ClassWriter;
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
        return _add(_intGetters, new IntMethodPropertyWriter(bpw, null, -1));
    }

    /*
    /**********************************************************
    /* Accessors
    /**********************************************************
     */

    public boolean hasEntries() {
        return !(_intGetters.isEmpty());
    }

    /*
    /**********************************************************
    /* Code generation
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

    private static void _addIntGetters(ClassWriter cw, List<IntMethodPropertyWriter> props,
            String beanClass)
    {
        // !!! TODO: proper handling
        IntMethodPropertyWriter prop = props.get(0);
        Method getter = ((AnnotatedMethod)prop.getMember()).getAnnotated();
        
        String genericSig = null; // no generics, can omit
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "intGetter", "(Ljava/lang/Object;I)I", genericSig, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 1); // load first arg (value object)
        mv.visitTypeInsn(CHECKCAST, beanClass); // cast to value type T
        mv.visitMethodInsn(INVOKEVIRTUAL, beanClass, getter.getName(), "()I");
        mv.visitInsn(IRETURN);
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();
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
