package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableIntMethodProperty
    extends OptimizedSettableBeanProperty<SettableIntMethodProperty>
{
    public SettableIntMethodProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableIntMethodProperty(SettableIntMethodProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableIntMethodProperty(SettableIntMethodProperty src, String name) {
        super(src, name);
    }

    @Override
    public SettableIntMethodProperty withName(String name) {
        return new SettableIntMethodProperty(this, name);
    }
    
    @Override
    public SettableIntMethodProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableIntMethodProperty(this, deser);
    }
    
    @Override
    public SettableIntMethodProperty withMutator(BeanPropertyMutator mut) {
        return new SettableIntMethodProperty(_originalSettable, mut, _propertyIndex);
    }
    
    @Override
    public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt,
            Object bean) throws IOException, JsonProcessingException
    {
        int value;
        if (jp.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            value = jp.getIntValue();
        } else {
            value = jp.getValueAsInt();
        }
        _propertyMutator.intSetter(bean, _propertyIndex, value);
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        // not optimal (due to boxing), but better than using reflection:
        _propertyMutator.intSetter(bean, _propertyIndex, ((Number) value).intValue());
    }
}
