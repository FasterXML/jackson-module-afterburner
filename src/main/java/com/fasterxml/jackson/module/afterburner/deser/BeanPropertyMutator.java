package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

/**
 * Abstract class that defines interface for implementations
 * that can be used proxy-like to change values of properties,
 * without using Reflection.
 */
public abstract class BeanPropertyMutator
{
    /**
     * Mutator we have to call if an access exception is encountered
     * durign operation
     */
    protected final SettableBeanProperty originalMutator;

    /**
     * Index of the property setter or field. Only used for error reporting
     * at this point.
     */
    protected final int index;

    // Intentionally not volatile for performance, worst case is we throw a few extra exceptions
    protected boolean broken = false;

    /*
    /********************************************************************** 
    /* Life-cycle methods
    /********************************************************************** 
     */
    
    /**
     * Default constructor used for creating a "blueprint" instance, from
     * which per-field/per-method instances specialize.
     */
    protected BeanPropertyMutator() {
        this(null, -1);
    }

    protected BeanPropertyMutator(SettableBeanProperty origM, int ix) {
        originalMutator = origM;
        index = ix;
    }

    /**
     * Mutant factory method called to create variant with proper fallback property
     * to call, index to use for error reporting.
     */
    public abstract BeanPropertyMutator with(SettableBeanProperty origM, int newIndex);

    /*
    /********************************************************************** 
    /* Methods to generate for field mutators, setters
    /********************************************************************** 
     */

    public abstract void intSetter(Object bean, int value) throws IOException;
    public abstract void longSetter(Object bean, long value) throws IOException;
    public abstract void booleanSetter(Object bean, boolean value) throws IOException;
    public abstract void stringSetter(Object bean, String value) throws IOException;
    public abstract void objectSetter(Object bean, Object value) throws IOException;
    
    public abstract void intField(Object bean, int value) throws IOException;
    public abstract void longField(Object bean, long value) throws IOException;
    public abstract void booleanField(Object bean, boolean value) throws IOException;
    public abstract void stringField(Object bean, String value) throws IOException;
    public abstract void objectField(Object bean, Object value) throws IOException;

    public void intSetter(Object bean, int propertyIndex, int value) {
        throw new UnsupportedOperationException("No intSetters defined");
    }
    public void longSetter(Object bean, int propertyIndex, long value) {
        throw new UnsupportedOperationException("No longSetters defined");
    }
    public void booleanSetter(Object bean, int propertyIndex, boolean value) {
        throw new UnsupportedOperationException("No booleanSetters defined");
    }
    public void stringSetter(Object bean, int propertyIndex, String value) {
        throw new UnsupportedOperationException("No stringSetters defined");
    }
    public void objectSetter(Object bean, int propertyIndex, Object value) {
        throw new UnsupportedOperationException("No objectSetters defined");
    }
    public void intField(Object bean, int propertyIndex, int value) {
        throw new UnsupportedOperationException("No intFields defined");
    }
    public void longField(Object bean, int propertyIndex, long value) {
        throw new UnsupportedOperationException("No longFields defined");
    }
    public void booleanField(Object bean, int propertyIndex, boolean value) {
        throw new UnsupportedOperationException("No booleanFields defined");
    }
    public void stringField(Object bean, int propertyIndex, String value) {
        throw new UnsupportedOperationException("No stringFields defined");
    }
    protected void objectField(Object bean, int propertyIndex, Object value) {
        throw new UnsupportedOperationException("No objectFields defined");
    }

    // // // Generated like:

    /*
    public void intSetter(Object bean, int value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
        }
        try {
            intSetter(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, value, e);
        } catch (SecurityException e) {
            _reportProblem(bean, value, e);
        }
    }

    public void intField(Object bean, int value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
        }
        try {
            intField(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, value, e);
        } catch (SecurityException e) {
            _reportProblem(bean, value, e);
        }
    }
    */

    /*
    /********************************************************************** 
    /* Fallback setters
    /********************************************************************** 
     */

    protected void _setOriginal(Object bean, int value) throws IOException {
        originalMutator.set(bean, value);
    }

    protected void _setOriginal(Object bean, long value) throws IOException {
        originalMutator.set(bean, value);
    }
    protected void _setOriginal(Object bean, boolean value) throws IOException {
        originalMutator.set(bean, value);
    }
    protected void _setOriginal(Object bean, String value) throws IOException {
        originalMutator.set(bean, value);
    }

    protected void _setOriginal(Object bean, Object value) throws IOException {
        originalMutator.set(bean, value);
    }
    
    /*
    /********************************************************************** 
    /* Helper methods for error handling
    /********************************************************************** 
     */

    protected synchronized void _reportProblem(Object bean, int value, Throwable e) throws IOException {
        if (!broken) {
            _printProblem(bean, e);
        }
        originalMutator.set(bean, value);
    }

    protected synchronized void _reportProblem(Object bean, long value, Throwable e) throws IOException {
        if (!broken) {
            _printProblem(bean, e);
        }
        originalMutator.set(bean, value);
    }

    protected synchronized void _reportProblem(Object bean, boolean value, Throwable e) throws IOException {
        if (!broken) {
            _printProblem(bean, e);
        }
        originalMutator.set(bean, value);
    }

    protected synchronized void _reportProblem(Object bean, String value, Throwable e) throws IOException {
        if (!broken) {
            _printProblem(bean, e);
        }
        originalMutator.set(bean, value);
    }
    
    protected synchronized void _reportProblem(Object bean, Object value, Throwable e) throws IOException {
        if (!broken) {
            _printProblem(bean, e);
        }
        originalMutator.set(bean, value);
    }

    private void _printProblem(Object bean, Throwable e) {
        broken = true;
        String msg = String.format("Disabling Afterburner deserialization for type %s, field #%d, due to access error (type %s, message=%s)%n",
                bean.getClass(), index,
                e.getClass().getName(), e.getMessage());
        Logger.getLogger(getClass().getName()).log(Level.WARNING, msg, e);
    }
}
