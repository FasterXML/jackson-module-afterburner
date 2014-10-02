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
    // Intentionally not volatile for performance, worst case is we throw a few extra exceptions
    private boolean broken = false;

    public void intSetter(SettableBeanProperty originalMutator, Object bean, int propertyIndex, int value)
        throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            intSetter(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        }
    }
    
    public void longSetter(SettableBeanProperty originalMutator, Object bean, int propertyIndex, long value)
        throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            longSetter(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        }
    }

    public void booleanSetter(SettableBeanProperty originalMutator, Object bean, int propertyIndex, boolean value)
            throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            booleanSetter(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        }
    }
    
    public void stringSetter(SettableBeanProperty originalMutator, Object bean, int propertyIndex, String value)
    throws IOException {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            stringSetter(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        }
    }
    public void objectSetter(SettableBeanProperty originalMutator, Object bean, int propertyIndex, Object value)
    throws IOException {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            objectSetter(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        }
    }

    public void intField(SettableBeanProperty originalMutator, Object bean, int propertyIndex, int value)
            throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            intField(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        }
    }

    public void longField(SettableBeanProperty originalMutator, Object bean, int propertyIndex, long value)
            throws IOException {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            longField(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        }
    }

    public void booleanField(SettableBeanProperty originalMutator, Object bean, int propertyIndex, boolean value)
            throws IOException {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            booleanField(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        }
    }
    
    public void stringField(SettableBeanProperty originalMutator, Object bean, int propertyIndex, String value)
            throws IOException {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            stringField(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        }
    }
    public void objectField(SettableBeanProperty originalMutator, Object bean, int propertyIndex, Object value)
    throws IOException {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            objectField(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, propertyIndex, e);
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            _reportProblem(bean, propertyIndex, e);
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

    protected void _reportProblem(Object bean, int index, Throwable e)
    {
        broken = true;
        String msg = String.format("Disabling Afterburner deserialization for type %s, field #%d, due to access error (type %s, message=%s)%n",
                bean.getClass(), index,
                e.getClass().getName(), e.getMessage());
        Logger.getLogger(getClass().getName()).log(Level.WARNING, msg, e);
    }
}
