package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableIntMethodProperty
    extends OptimizedSettableBeanProperty<SettableIntMethodProperty>
{
    private static final long serialVersionUID = 1L;

    public SettableIntMethodProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableIntMethodProperty(SettableIntMethodProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableIntMethodProperty(SettableIntMethodProperty src, PropertyName name) {
        super(src, name);
    }

    @Override
    public SettableIntMethodProperty withName(PropertyName name) {
        return new SettableIntMethodProperty(this, name);
    }
    
    @Override
    public SettableIntMethodProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableIntMethodProperty(this, deser);
    }
    
    @Override
    public SettableIntMethodProperty withMutator(BeanPropertyMutator mut) {
        return new SettableIntMethodProperty(_originalSettable, mut, _optimizedIndex);
    }

    /*
    /********************************************************************** 
    /* Deserialization
    /********************************************************************** 
     */
    
    @Override
    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt,
            Object bean) throws IOException
    {
        int v = p.hasToken(JsonToken.VALUE_NUMBER_INT) ? p.getIntValue() : _deserializeInt(p, ctxt);
        _propertyMutator.intSetter(bean, v);
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        // not optimal (due to boxing), but better than using reflection:
        _propertyMutator.intSetter(bean, ((Number) value).intValue());
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser p,
            DeserializationContext ctxt, Object instance)
        throws IOException
    {
        int v = p.hasToken(JsonToken.VALUE_NUMBER_INT) ? p.getIntValue() : _deserializeInt(p, ctxt);
        return setAndReturn(instance, v);
    }    
}
