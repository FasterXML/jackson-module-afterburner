package com.fasterxml.jackson.module.afterburner.deser;

import static org.codehaus.jackson.org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.codehaus.jackson.org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.codehaus.jackson.org.objectweb.asm.Opcodes.V1_5;

import java.lang.reflect.*;

import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.impl.StdValueInstantiator;
import org.codehaus.jackson.map.introspect.AnnotatedWithParams;
import org.codehaus.jackson.org.objectweb.asm.ClassWriter;

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
        String generatedClass = internalClassName(srcName);

        Class<?> impl = null;
        try {
            impl = loader.loadClass(srcName);
        } catch (ClassNotFoundException e) { }
        if (impl == null) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            String superClass = internalClassName(BeanPropertyMutator.class.getName());
            
            cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, generatedClass, null, superClass, null);
            cw.visitSource(srcName + ".java", null);
            // Can't use default constructor here
            
            if (ctor != null) {
                addCreator(cw, ctor);
            } else {
                addCreator(cw, factory);
            }
        }
        try {
            return (ValueInstantiator) impl.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate accessor class '"+srcName+"': "+e.getMessage(), e);
        }
    }

    protected void addCreator(ClassWriter cw, Constructor<?> ctor)
    {
        
    }

    protected void addCreator(ClassWriter cw, Method factory)
    {
        
    }
}
