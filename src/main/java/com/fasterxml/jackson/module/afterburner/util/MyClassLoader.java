package com.fasterxml.jackson.module.afterburner.util;

/**
 * Class loader that is needed to load generated classes.
 */
public class MyClassLoader extends ClassLoader
{
    public MyClassLoader(ClassLoader parent)
    {
        super(parent);
    }
    
    /**
     * @param targetClass Interface or abstract class that class to load should extend or 
     *   implement
     */
    public Class<?> loadAndResolve(String className, byte[] byteCode, Class<?> targetClass)
        throws IllegalArgumentException
    {
        // First things first: just to be sure; maybe we have already loaded it?
        Class<?> old = findLoadedClass(className);
        if (old != null && targetClass.isAssignableFrom(old)) {
            return old;
        }
        
        Class<?> impl;
        try {
            impl = defineClass(className, byteCode, 0, byteCode.length);
        } catch (LinkageError e) {
            throw new IllegalArgumentException("Failed to load class '"+className+"': "+e.getMessage() ,e);
        }
        // important: must also resolve the class...
        resolveClass(impl);
        return impl;
    }
}
