package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;
import java.lang.annotation.Annotation;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;

/**
 * Base class for concrete type-specific {@link SettableBeanProperty}
 * implementations.
 */
abstract class OptimizedSettableBeanProperty<T extends OptimizedSettableBeanProperty<T>>
    extends SettableBeanProperty
{
    /**
     * We will need to keep the original instance handy as
     * some calls are best just delegated
     */
    protected final SettableBeanProperty _originalSettable;
    
    protected final BeanPropertyMutator _propertyMutator;
    protected final int _propertyIndex;
    
    public OptimizedSettableBeanProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src);
        _originalSettable = src;
        _propertyMutator = mutator;
        _propertyIndex = index;
    }

    protected OptimizedSettableBeanProperty(OptimizedSettableBeanProperty<T> src,
            JsonDeserializer<Object> deser)
    {
        super(src, deser);
        _originalSettable = src;
        _propertyMutator = src._propertyMutator;
        _propertyIndex = src._propertyIndex;
    }

    protected OptimizedSettableBeanProperty(OptimizedSettableBeanProperty<T> src,
            String name)
    {
        super(src, name);
        _originalSettable = src;
        _propertyMutator = src._propertyMutator;
        _propertyIndex = src._propertyIndex;
    }
    
    public abstract T withMutator(BeanPropertyMutator mut);

    public abstract T withValueDeserializer(JsonDeserializer<Object> deser);
    
    @Override
    public abstract void deserializeAndSet(JsonParser jp, DeserializationContext ctxt,
            Object arg2) throws IOException, JsonProcessingException;

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> ann) {
        return _originalSettable.getAnnotation(ann);
    }

    @Override
    public AnnotatedMember getMember() {
        return _originalSettable.getMember();
    }

    @Override
    public abstract void set(Object bean, Object value) throws IOException;

    /**
     * Helper method for coercing JSON values other than Strings into
     * Java String value.
     */
    protected final String _convertToString(JsonParser jp, DeserializationContext ctxt,
            JsonToken curr)
        throws IOException, JsonProcessingException
    {
        if (curr == JsonToken.VALUE_NULL) {
            return null;
        }
        if (curr == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = jp.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (ob instanceof byte[]) {
                return Base64Variants.getDefaultVariant().encode((byte[]) ob, false);
            }
            return ob.toString();
        }
        // Can deserialize any scalar value
        if (curr.isScalarValue()) {
            return jp.getText();
        }
        // but not markers:
        throw ctxt.mappingException(String.class);
    }
}
