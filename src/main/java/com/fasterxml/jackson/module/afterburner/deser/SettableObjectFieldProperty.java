package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableObjectFieldProperty
    extends OptimizedSettableBeanProperty<SettableObjectFieldProperty>
{
    private static final long serialVersionUID = 1L;

    public SettableObjectFieldProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableObjectFieldProperty(SettableObjectFieldProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableObjectFieldProperty(SettableObjectFieldProperty src, PropertyName name) {
        super(src, name);
    }
    
    @Override
    public SettableObjectFieldProperty withName(PropertyName name) {
        return new SettableObjectFieldProperty(this, name);
    }
    
    @Override
    public SettableObjectFieldProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableObjectFieldProperty(this, deser);
    }
    
    @Override
    public SettableObjectFieldProperty withMutator(BeanPropertyMutator mut) {
        return new SettableObjectFieldProperty(_originalSettable, mut, _optimizedIndex);
    }

    /*
    /********************************************************************** 
    /* Deserialization
    /********************************************************************** 
     */
    
    @Override
    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt,
            Object bean) throws IOException
    {
        set(bean, deserialize(p, ctxt));
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        _propertyMutator.objectField(bean, value);
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser p,
            DeserializationContext ctxt, Object instance) throws IOException
    {
        return setAndReturn(instance, deserialize(p, ctxt));
    }    
}
