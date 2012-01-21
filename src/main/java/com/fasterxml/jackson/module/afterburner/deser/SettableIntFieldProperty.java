package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableIntFieldProperty
    extends OptimizedSettableBeanProperty<SettableIntFieldProperty>
{
    public SettableIntFieldProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableIntFieldProperty(SettableIntFieldProperty src,
            JsonDeserializer<Object> deser)
    {
        super(src, deser);
    }

    public SettableIntFieldProperty(SettableIntFieldProperty src, String name) {
        super(src, name);
    }
    
    @Override
    public SettableIntFieldProperty withName(String name) {
        return new SettableIntFieldProperty(this, name);
    }
    
    @Override
    public SettableIntFieldProperty withValueDeserializer(JsonDeserializer<Object> deser) {
        return new SettableIntFieldProperty(this, deser);
    }
    
    @Override
    public SettableIntFieldProperty withMutator(BeanPropertyMutator mut) {
        return new SettableIntFieldProperty(_originalSettable, mut, _propertyIndex);
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
        _propertyMutator.intField(bean, _propertyIndex, value);
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        // not optimal (due to boxing), but better than using reflection:
        _propertyMutator.intField(bean, _propertyIndex, ((Number) value).intValue());
    }
}
