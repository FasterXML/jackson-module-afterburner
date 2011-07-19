package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.deser.SettableBeanProperty;

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
