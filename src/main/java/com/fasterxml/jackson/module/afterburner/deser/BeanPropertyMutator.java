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
    private boolean broken = false;

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
    /* Setter calls
    /********************************************************************** 
     */

    public void intSetter(Object bean, int value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            intSetter(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        }
    }
    
    public void longSetter(Object bean, long value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            longSetter(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        }
    }

    public void booleanSetter(Object bean, boolean value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            booleanSetter(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        }
    }
    
    public void stringSetter(Object bean, String value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            stringSetter(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        }
    }
    public void objectSetter(Object bean, Object value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            objectSetter(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        }
    }

    /*
    /********************************************************************** 
    /* Field assignments
    /********************************************************************** 
     */
    
    public void intField(Object bean, int value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            intField(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        }
    }

    public void longField(Object bean, long value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            longField(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        }
    }

    public void booleanField(Object bean, boolean value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            booleanField(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        }
    }
    
    public void stringField(Object bean, String value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            stringField(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        }
    }
    public void objectField(Object bean, Object value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            objectField(bean, index, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            originalMutator.set(bean, value);
        }
    }

    protected void intSetter(Object bean, int propertyIndex, int value) {
        throw new UnsupportedOperationException("No intSetters defined");
    }
    protected void longSetter(Object bean, int propertyIndex, long value) {
        throw new UnsupportedOperationException("No longSetters defined");
    }
    protected void booleanSetter(Object bean, int propertyIndex, boolean value) {
        throw new UnsupportedOperationException("No booleanSetters defined");
    }
    protected void stringSetter(Object bean, int propertyIndex, String value) {
        throw new UnsupportedOperationException("No stringSetters defined");
    }
    protected void objectSetter(Object bean, int propertyIndex, Object value) {
        throw new UnsupportedOperationException("No objectSetters defined");
    }
    protected void intField(Object bean, int propertyIndex, int value) {
        throw new UnsupportedOperationException("No intFields defined");
    }
    protected void longField(Object bean, int propertyIndex, long value) {
        throw new UnsupportedOperationException("No longFields defined");
    }
    protected void booleanField(Object bean, int propertyIndex, boolean value) {
        throw new UnsupportedOperationException("No booleanFields defined");
    }
    protected void stringField(Object bean, int propertyIndex, String value) {
        throw new UnsupportedOperationException("No stringFields defined");
    }
    protected void objectField(Object bean, int propertyIndex, Object value) {
        throw new UnsupportedOperationException("No objectFields defined");
    }

    /*
    /********************************************************************** 
    /* Helper methods
    /********************************************************************** 
     */

    private void _reportProblem(Object bean, Throwable e)
    {
        broken = true;
        String msg = String.format("Disabling Afterburner deserialization for type %s, field #%d, due to access error (type %s, message=%s)%n",
                bean.getClass(), index,
                e.getClass().getName(), e.getMessage());
        Logger.getLogger(getClass().getName()).log(Level.WARNING, msg, e);
    }
}
