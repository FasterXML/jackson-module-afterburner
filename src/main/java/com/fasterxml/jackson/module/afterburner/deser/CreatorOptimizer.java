package com.fasterxml.jackson.module.afterburner.deser;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.fasterxml.jackson.module.afterburner.util.DynamicPropertyAccessorBase;
import com.fasterxml.jackson.module.afterburner.util.MyClassLoader;

/**
 * Helper class that tries to generate {@link ValueInstantiator} class
 * that calls constructors and/or factory methods directly, instead
 * of using Reflection.
 */
public class CreatorOptimizer
    extends DynamicPropertyAccessorBase
{
    protected final Class<?> _valueClass;
    
    protected final MyClassLoader _classLoader;
    
    protected final StdValueInstantiator _originalInstantiator;

    public CreatorOptimizer(Class<?> valueClass, MyClassLoader classLoader,
            StdValueInstantiator orig)
    {
        _valueClass = valueClass;
        _classLoader = classLoader;
        _originalInstantiator = orig;
    }

    public ValueInstantiator createOptimized()
    {
        // for now, only consider need to handle default creator
        AnnotatedWithParams defaultCreator = _originalInstantiator.getDefaultCreator();
        if (defaultCreator != null) {
            AnnotatedElement elem = defaultCreator.getAnnotated();
            if (elem instanceof Constructor<?>) {
                return createSubclass((Constructor<?>) elem, null);
            }
            if (elem instanceof Method) {
                Method m = (Method) elem;
                if (Modifier.isStatic(m.getModifiers())) {
                    return createSubclass(null, m);
                }
            }
        }
        return null;
    }

    protected ValueInstantiator createSubclass(Constructor<?> ctor, Method factory)
    {
        MyClassLoader loader = (_classLoader == null) ?
            new MyClassLoader(_valueClass.getClassLoader(), true) : _classLoader;
        String srcName = _valueClass.getName() + "$Creator4JacksonDeserializer";
        Class<?> impl = null;
        try {
            impl = loader.loadClass(srcName);
        } catch (ClassNotFoundException e) { }
        if (impl == null) {
            byte[] bytecode = generateOptimized(srcName, ctor, factory);
            impl = loader.loadAndResolve(srcName, bytecode);
        }
        ValueInstantiator inst;
        try {
            inst = (ValueInstantiator) impl.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate accessor class '"+srcName+"': "+e.getMessage(), e);
        }
        return inst;
    }

    protected byte[] generateOptimized(String srcName, Constructor<?> ctor, Method factory)
    {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            String superClass = internalClassName(OptimizedValueInstantiator.class.getName());
            String generatedClass = internalClassName(srcName);
            
            cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, generatedClass, null, superClass, null);
            cw.visitSource(srcName + ".java", null);

            // First: must define 2 constructors:
            // (a) default constructor, for creating bogus instance (just calls default instance)
            // (b) copy-constructor which takes StdValueInstantiator instance, passes to superclass
            final String optimizedValueInstDesc = Type.getDescriptor(OptimizedValueInstantiator.class);
            final String stdValueInstDesc = Type.getDescriptor(StdValueInstantiator.class);

            // default (no-arg) constructor:
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, superClass, "<init>", "()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            // then single-arg constructor
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "("+stdValueInstDesc+")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, superClass, "<init>", "("+stdValueInstDesc+")V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            // and then non-static factory method to use second constructor (implements base-class method)
            // protected abstract OptimizedValueInstantiator with(StdValueInstantiator src);
            mv = cw.visitMethod(ACC_PUBLIC, "with", "("
                    +stdValueInstDesc+")"+optimizedValueInstDesc, null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, generatedClass);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, generatedClass, "<init>", "("+stdValueInstDesc+")V");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            // And then override: public Object createUsingDefault()
            mv = cw.visitMethod(ACC_PUBLIC, "createUsingDefault", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            
            if (ctor != null) {
                addCreator(mv, ctor);
            } else {
                addCreator(mv, factory);
            }
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            cw.visitEnd();
            return cw.toByteArray();
    }

    protected void addCreator(MethodVisitor mv, Constructor<?> ctor)
    {
        String valueClassInternal = Type.getInternalName(ctor.getDeclaringClass());
        mv.visitTypeInsn(NEW, valueClassInternal);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, valueClassInternal, "<init>", "()V");
    }

    protected void addCreator(MethodVisitor mv, Method factory)
    {
        Class<?> valueClass = factory.getReturnType();
        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(factory.getDeclaringClass()),
                factory.getName(), "()"+Type.getDescriptor(valueClass));
    }
}
