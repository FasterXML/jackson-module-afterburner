package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.SettableBeanProperty;

public class SettableObjectFieldProperty
extends OptimizedSettableBeanProperty<SettableObjectFieldProperty>
{
    public SettableObjectFieldProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    @Override
    public SettableObjectFieldProperty withMutator(BeanPropertyMutator mut) {
        return new SettableObjectFieldProperty(_originalSettable, mut, _propertyIndex);
    }
    
    @Override
    public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt,
            Object bean) throws IOException, JsonProcessingException
    {
        set(bean, deserialize(jp, ctxt));
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        _propertyMutator.objectField(bean, _propertyIndex, value);
    }
}
