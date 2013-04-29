package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

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
    throws IOException {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            intSetter(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        }
    }
    public void longSetter(SettableBeanProperty originalMutator, Object bean, int propertyIndex, long value)
    throws IOException {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            longSetter(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
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
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
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
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        }
    }
    public void intField(SettableBeanProperty originalMutator, Object bean, int propertyIndex, int value)
    throws IOException {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            intField(bean, propertyIndex, value);
        } catch (IllegalAccessError e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
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
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
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
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
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
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        } catch (SecurityException e) {
            System.err.format("Disabling Afterburner for %s due to access error%n", bean.getClass());
            e.printStackTrace(); // TODO
            broken = true;
            originalMutator.set(bean, value);
        }
    }

    protected void intSetter(Object bean, int propertyIndex, int value) {
        throw new UnsupportedOperationException("No intSetters defined");
    }
    protected void longSetter(Object bean, int propertyIndex, long value) {
        throw new UnsupportedOperationException("No longSetters defined");
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
    protected void stringField(Object bean, int propertyIndex, String value) {
        throw new UnsupportedOperationException("No stringFields defined");
    }
    protected void objectField(Object bean, int propertyIndex, Object value) {
        throw new UnsupportedOperationException("No objectFields defined");
    }
}
