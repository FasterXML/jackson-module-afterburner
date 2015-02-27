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
     * Index of the property setter or field.
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

    public abstract BeanPropertyMutator with(SettableBeanProperty origM, int newIndex);

    /*
    /********************************************************************** 
    /* Setter calls
    /********************************************************************** 
     */

    public final void intSetter(Object bean, int value) throws IOException
    {
        if (!broken) {
            try {
                _intSetter(bean, value);
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        originalMutator.set(bean, value);
    }
    
    public final void longSetter(Object bean, long value) throws IOException
    {
        if (!broken) {
            try {
                _longSetter(bean, value);
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        originalMutator.set(bean, value);
    }

    public final void booleanSetter(Object bean, boolean value) throws IOException
    {
        if (!broken) {
            try {
                _booleanSetter(bean, value);
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        originalMutator.set(bean, value);
    }
    
    public final void stringSetter(Object bean, String value) throws IOException
    {
        if (!broken) {
            try {
                _stringSetter(bean, value);
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        originalMutator.set(bean, value);
    }
    public final void objectSetter(Object bean, Object value) throws IOException
    {
        if (!broken) {
            try {
                _objectSetter(bean, value);
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        originalMutator.set(bean, value);
    }

    /*
    /********************************************************************** 
    /* Field assignments
    /********************************************************************** 
     */
    
    public final void intField(Object bean, int value) throws IOException
    {
        if (!broken) {
            try {
                _intField(bean, value);
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        originalMutator.set(bean, value);
    }

    public final void longField(Object bean, long value) throws IOException
    {
        if (!broken) {
            try {
                _longField(bean, value);
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        originalMutator.set(bean, value);
    }

    public final void booleanField(Object bean, boolean value) throws IOException
    {
        if (!broken) {
            try {
                _booleanField(bean, value);
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        originalMutator.set(bean, value);
    }
    
    public final void stringField(Object bean, String value) throws IOException
    {
        if (!broken) {
            try {
                _stringField(bean, value);
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        originalMutator.set(bean, value);
    }

    public final void objectField(Object bean, Object value) throws IOException
    {
        if (!broken) {
            try {
                _objectField(bean, value);
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        originalMutator.set(bean, value);
    }

    protected void _intSetter(Object bean, int value) {
        throw new UnsupportedOperationException("No intSetters defined");
    }
    protected void _longSetter(Object bean, long value) {
        throw new UnsupportedOperationException("No longSetters defined");
    }
    protected void _booleanSetter(Object bean, boolean value) {
        throw new UnsupportedOperationException("No booleanSetters defined");
    }
    protected void _stringSetter(Object bean, String value) {
        throw new UnsupportedOperationException("No stringSetters defined");
    }
    protected void _objectSetter(Object bean, Object value) {
        throw new UnsupportedOperationException("No objectSetters defined");
    }

    protected void _intField(Object bean, int value) {
        throw new UnsupportedOperationException("No intFields defined");
    }
    protected void _longField(Object bean, long value) {
        throw new UnsupportedOperationException("No longFields defined");
    }
    protected void _booleanField(Object bean, boolean value) {
        throw new UnsupportedOperationException("No booleanFields defined");
    }
    protected void _stringField(Object bean, String value) {
        throw new UnsupportedOperationException("No stringFields defined");
    }
    protected void _objectField(Object bean, Object value) {
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
