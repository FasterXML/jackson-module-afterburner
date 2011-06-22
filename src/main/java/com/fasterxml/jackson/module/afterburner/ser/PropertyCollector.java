package com.fasterxml.jackson.module.afterburner.ser;

import java.util.*;

import static org.codehaus.jackson.org.objectweb.asm.Opcodes.*;

import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.org.objectweb.asm.ClassWriter;
import org.codehaus.jackson.org.objectweb.asm.MethodVisitor;

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
        return _add(_intGetters, new IntMethodPropertyWriter(bpw, null, _intGetters.size()));
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

    public byte[] generateAccessorClass(Class<?> beanType)
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        String generatedName = beanType.getName() + "$Access4Jackson";
        String generatedClass = internalClassName(generatedName);
        String superClass = internalClassName(BeanPropertyAccessor.class.getName());
        
        // muchos important: level at least 1.5 to get generics!!!
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, generatedClass, null, superClass, null);
        cw.visitSource(generatedName + ".java", null);
        generateDefaultConstructor(cw, superClass);

        if (!_intGetters.isEmpty()) {
            // !!! TBI
        }
        cw.visitEnd();
        return cw.toByteArray();
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
