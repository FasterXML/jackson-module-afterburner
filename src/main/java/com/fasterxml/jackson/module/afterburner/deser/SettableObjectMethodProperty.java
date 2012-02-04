package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableObjectMethodProperty
    extends OptimizedSettableBeanProperty<SettableObjectMethodProperty>
{
    public SettableObjectMethodProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableObjectMethodProperty(SettableObjectMethodProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableObjectMethodProperty(SettableObjectMethodProperty src, String name) {
        super(src, name);
    }
    
    @Override
    public SettableObjectMethodProperty withName(String name) {
        return new SettableObjectMethodProperty(this, name);
    }
    
    @Override
    public SettableObjectMethodProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableObjectMethodProperty(this, deser);
    }
    
    @Override
    public SettableObjectMethodProperty withMutator(BeanPropertyMutator mut) {
        return new SettableObjectMethodProperty(_originalSettable, mut, _propertyIndex);
    }
    
    @Override
    public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt,
            Object bean) throws IOException, JsonProcessingException
    {
        set(bean, deserialize(jp, ctxt));
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        _propertyMutator.objectSetter(bean, _propertyIndex, value);
    }
}
    