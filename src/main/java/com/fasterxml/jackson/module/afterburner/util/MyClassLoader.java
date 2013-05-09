package com.fasterxml.jackson.module.afterburner.util;

import java.lang.reflect.Method;

/**
 * Class loader that is needed to load generated classes.
 */
public class MyClassLoader extends ClassLoader
{
    /**
     * Flag that determines if we should first try to load new class
     * using parent class loader or not; this may be done to try to
     * force access to protected/package-access properties.
     */
    protected final boolean _cfgUseParentLoader;
    
    public MyClassLoader(ClassLoader parent, boolean tryToUseParent)
    {
        super(parent);
        _cfgUseParentLoader = tryToUseParent;
    }

    /**
     * Helper method called to check whether it is acceptable to create a new
     * class in package that given class is part of.
     * This is used to prevent certain class of failures, related to access
     * limitations: for example, we can not add classes in sealed packages,
     * or core Java packages (java.*).
     * 
     * @since 2.2.1
     */
    public static boolean canAddClassInPackageOf(Class<?> cls)
    {
        // TODO: we should be able to do this, but currently causes IncompatibleClassChangeError
        if (cls.isInterface()) {
            return false;
        }

        final Package beanPackage = cls.getPackage();
        if (beanPackage != null) {
            // 01-May-2013, tatu: How about "javax."?
            if (beanPackage.isSealed() || beanPackage.getName().startsWith("java.")) {
                return false;
            }
        } 
        return true;
    }
    
    /**
     * @param className Interface or abstract class that class to load should extend or 
     *   implement
     */
    public Class<?> loadAndResolve(String className, byte[] byteCode)
        throws IllegalArgumentException
    {
        // First things first: just to be sure; maybe we have already loaded it?
        Class<?> old = findLoadedClass(className);
        if (old != null) {
            return old;
        }
        
        Class<?> impl;
        
        // First: let's try calling it directly on parent, to be able to access protected/package-access stuff:
        if (_cfgUseParentLoader) {
            try {
                Method method = ClassLoader.class.getDeclaredMethod("defineClass", 
                        new Class[] {String.class, byte[].class, int.class,
                        int.class});
                method.setAccessible(true);
                return (Class<?>)method.invoke(getParent(),
                        new Object[] { className, byteCode, Integer.valueOf(0), Integer.valueOf(byteCode.length)});
            } catch (Exception e) { }
        }

        // but if that doesn't fly, try to do it from our own class loader
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
