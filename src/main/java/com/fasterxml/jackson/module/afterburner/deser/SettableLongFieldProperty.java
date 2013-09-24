package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableLongFieldProperty
    extends OptimizedSettableBeanProperty<SettableLongFieldProperty>
{
    private static final long serialVersionUID = 7076292232493011035L;

    public SettableLongFieldProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableLongFieldProperty(SettableLongFieldProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableLongFieldProperty(SettableLongFieldProperty src, PropertyName name) {
        super(src, name);
    }
    
    @Override
    public SettableLongFieldProperty withName(PropertyName name) {
        return new SettableLongFieldProperty(this, name);
    }
    
    @Override
    public SettableLongFieldProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableLongFieldProperty(this, deser);
    }
    
    @Override
    public SettableLongFieldProperty withMutator(BeanPropertyMutator mut) {
        return new SettableLongFieldProperty(_originalSettable, mut, _optimizedIndex);
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
        _propertyMutator.longField(_originalSettable, bean, _optimizedIndex, _deserializeLong(jp, ctxt));
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        // not optimal (due to boxing), but better than using reflection:
        _propertyMutator.longField(_originalSettable, bean, _optimizedIndex, ((Number) value).longValue());
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser jp,
            DeserializationContext ctxt, Object instance)
        throws IOException, JsonProcessingException
    {
        return setAndReturn(instance, _deserializeLong(jp, ctxt));
    }    
}
