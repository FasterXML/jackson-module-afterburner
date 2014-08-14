package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableStringFieldProperty
    extends OptimizedSettableBeanProperty<SettableStringFieldProperty>
{
    private static final long serialVersionUID = 1L;

    public SettableStringFieldProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableStringFieldProperty(SettableStringFieldProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableStringFieldProperty(SettableStringFieldProperty src, PropertyName name) {
        super(src, name);
    }
    
    @Override
    public SettableStringFieldProperty withName(PropertyName name) {
        return new SettableStringFieldProperty(this, name);
    }
    
    @Override
    public SettableStringFieldProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableStringFieldProperty(this, deser);
    }
    
    @Override
    public SettableStringFieldProperty withMutator(BeanPropertyMutator mut) {
        return new SettableStringFieldProperty(_originalSettable, mut, _optimizedIndex);
    }

    /*
    /********************************************************************** 
    /* Deserialization
    /********************************************************************** 
     */

    @Override
    public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt,
            Object bean) throws IOException, JsonProcessingException
    {
        _propertyMutator.stringField(_originalSettable, bean, _optimizedIndex, _deserializeString(jp, ctxt));
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        _propertyMutator.stringField(_originalSettable, bean, _optimizedIndex, (String) value);
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser jp,
            DeserializationContext ctxt, Object instance)
        throws IOException, JsonProcessingException
    {
        return setAndReturn(instance, _deserializeString(jp, ctxt));
    }
}
