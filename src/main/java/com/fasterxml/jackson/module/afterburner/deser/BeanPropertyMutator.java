package com.fasterxml.jackson.module.afterburner.deser;

/**
 * Abstract class that defines interface for implementations
 * that can be used proxy-like to change values of properties,
 * without using Reflection.
 */
public abstract class BeanPropertyMutator
{
    public void intSetter(Object bean, int propertyIndex, int value) {
        throw new UnsupportedOperationException("No intSetters defined");
    }
    public void longSetter(Object bean, int propertyIndex, long value) {
        throw new UnsupportedOperationException("No longSetters defined");
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
    public void stringField(Object bean, int propertyIndex, String value) {
        throw new UnsupportedOperationException("No stringFields defined");
    }
    public void objectField(Object bean, int propertyIndex, Object value) {
        throw new UnsupportedOperationException("No objectFields defined");
    }
}
