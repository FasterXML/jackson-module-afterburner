package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableLongFieldProperty
    extends OptimizedSettableBeanProperty<SettableLongFieldProperty>
{
    public SettableLongFieldProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableLongFieldProperty(SettableLongFieldProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableLongFieldProperty(SettableLongFieldProperty src, String name) {
        super(src, name);
    }
    
    @Override
    public SettableLongFieldProperty withName(String name) {
        return new SettableLongFieldProperty(this, name);
    }
    
    @Override
    public SettableLongFieldProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableLongFieldProperty(this, deser);
    }
    
    @Override
    public SettableLongFieldProperty withMutator(BeanPropertyMutator mut) {
        return new SettableLongFieldProperty(_originalSettable, mut, _propertyIndex);
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
        _propertyMutator.longField(bean, _propertyIndex, value);
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        // not optimal (due to boxing), but better than using reflection:
        _propertyMutator.longField(bean, _propertyIndex, ((Number) value).longValue());
    }
}
