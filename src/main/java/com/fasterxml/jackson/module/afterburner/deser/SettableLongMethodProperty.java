package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableLongMethodProperty
    extends OptimizedSettableBeanProperty<SettableLongMethodProperty>
{
    public SettableLongMethodProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableLongMethodProperty(SettableLongMethodProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableLongMethodProperty(SettableLongMethodProperty src, String name) {
        super(src, name);
    }
    
    @Override
    public SettableLongMethodProperty withName(String name) {
        return new SettableLongMethodProperty(this, name);
    }
    
    @Override
    public SettableLongMethodProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableLongMethodProperty(this, deser);
    }
    
    @Override
    public SettableLongMethodProperty withMutator(BeanPropertyMutator mut) {
        return new SettableLongMethodProperty(_originalSettable, mut, _propertyIndex);
    }
    
    @Override
    public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt,
            Object bean) throws IOException, JsonProcessingException
    {
        long value;
        if (jp.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            value = jp.getLongValue();
        } else {
            value = jp.getValueAsLong();
        }
        _propertyMutator.longSetter(bean, _propertyIndex, value);
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        // not optimal (due to boxing), but better than using reflection:
        _propertyMutator.longSetter(bean, _propertyIndex, ((Number) value).longValue());
    }
}
