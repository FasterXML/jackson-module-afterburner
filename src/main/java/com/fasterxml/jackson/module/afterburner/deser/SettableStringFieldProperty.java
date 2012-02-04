package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableStringFieldProperty
    extends OptimizedSettableBeanProperty<SettableStringFieldProperty>
{
    public SettableStringFieldProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableStringFieldProperty(SettableStringFieldProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableStringFieldProperty(SettableStringFieldProperty src, String name) {
        super(src, name);
    }
    
    @Override
    public SettableStringFieldProperty withName(String name) {
        return new SettableStringFieldProperty(this, name);
    }
    
    @Override
    public SettableStringFieldProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableStringFieldProperty(this, deser);
    }
    
    @Override
    public SettableStringFieldProperty withMutator(BeanPropertyMutator mut) {
        return new SettableStringFieldProperty(_originalSettable, mut, _propertyIndex);
    }
    
    @Override
    public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt,
            Object bean) throws IOException, JsonProcessingException
    {
        String value;
        JsonToken curr = jp.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING) {
            value = jp.getText();
        } else {
            value = _convertToString(jp, ctxt, curr);
        }
        _propertyMutator.stringField(bean, _propertyIndex, value);
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        _propertyMutator.stringField(bean, _propertyIndex, (String) value);
    }
}
