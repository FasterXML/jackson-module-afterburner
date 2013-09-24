package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableLongMethodProperty
    extends OptimizedSettableBeanProperty<SettableLongMethodProperty>
{
    private static final long serialVersionUID = -2972029911676409547L;

    public SettableLongMethodProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableLongMethodProperty(SettableLongMethodProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableLongMethodProperty(SettableLongMethodProperty src, PropertyName name) {
        super(src, name);
    }
    
    @Override
    public SettableLongMethodProperty withName(PropertyName name) {
        return new SettableLongMethodProperty(this, name);
    }
    
    @Override
    public SettableLongMethodProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableLongMethodProperty(this, deser);
    }
    
    @Override
    public SettableLongMethodProperty withMutator(BeanPropertyMutator mut) {
        return new SettableLongMethodProperty(_originalSettable, mut, _optimizedIndex);
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
        _propertyMutator.longSetter(_originalSettable, bean, _optimizedIndex, _deserializeLong(jp, ctxt));
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        // not optimal (due to boxing), but better than using reflection:
        _propertyMutator.longSetter(_originalSettable, bean, _optimizedIndex, ((Number) value).longValue());
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser jp,
            DeserializationContext ctxt, Object instance)
        throws IOException, JsonProcessingException
    {
        return setAndReturn(instance, _deserializeLong(jp, ctxt));
    }    
}
