package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableStringMethodProperty
    extends OptimizedSettableBeanProperty<SettableStringMethodProperty>
{
    public SettableStringMethodProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableStringMethodProperty(SettableStringMethodProperty src, JsonDeserializer<Object> deser) {
        super(src, deser);
    }

    public SettableStringMethodProperty(SettableStringMethodProperty src, String name) {
        super(src, name);
    }
    
    @Override
    public SettableStringMethodProperty withName(String name) {
        return new SettableStringMethodProperty(this, name);
    }
    
    @Override
    public SettableStringMethodProperty withValueDeserializer(JsonDeserializer<Object> deser) {
        return new SettableStringMethodProperty(this, deser);
    }
    
    @Override
    public SettableStringMethodProperty withMutator(BeanPropertyMutator mut) {
        return new SettableStringMethodProperty(_originalSettable, mut, _propertyIndex);
    }

    // Copied from StdDeserializer.StringDeserializer:
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
        _propertyMutator.stringSetter(bean, _propertyIndex, value);
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        _propertyMutator.stringSetter(bean, _propertyIndex, (String) value);
    }
}
