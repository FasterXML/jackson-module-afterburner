package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.SettableBeanProperty;

public class SettableLongFieldProperty
    extends OptimizedSettableBeanProperty<SettableLongFieldProperty>
{
    public SettableLongFieldProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    @Override
    public SettableLongFieldProperty withMutator(BeanPropertyMutator mut) {
        return new SettableLongFieldProperty(_originalSettable, mut, _propertyIndex);
    }
    
    @Override
    public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt,
            Object bean) throws IOException, JsonProcessingException
    {
        long value = jp.getValueAsLong();
        _propertyMutator.longField(bean, _propertyIndex, value);
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        // not optimal (due to boxing), but better than using reflection:
        _propertyMutator.longField(bean, _propertyIndex, ((Number) value).longValue());
    }
}
