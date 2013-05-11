package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableObjectFieldProperty
    extends OptimizedSettableBeanProperty<SettableObjectFieldProperty>
{
    private static final long serialVersionUID = -580365444295893196L;

    public SettableObjectFieldProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableObjectFieldProperty(SettableObjectFieldProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableObjectFieldProperty(SettableObjectFieldProperty src, String name) {
        super(src, name);
    }
    
    @Override
    public SettableObjectFieldProperty withName(String name) {
        return new SettableObjectFieldProperty(this, name);
    }
    
    @Override
    public SettableObjectFieldProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableObjectFieldProperty(this, deser);
    }
    
    @Override
    public SettableObjectFieldProperty withMutator(BeanPropertyMutator mut) {
        return new SettableObjectFieldProperty(_originalSettable, mut, _propertyIndex);
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
        set(bean, deserialize(jp, ctxt));
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        _propertyMutator.objectField(_originalSettable, bean, _propertyIndex, value);
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser jp,
            DeserializationContext ctxt, Object instance)
        throws IOException, JsonProcessingException
    {
        return setAndReturn(instance, deserialize(jp, ctxt));
    }    
}
