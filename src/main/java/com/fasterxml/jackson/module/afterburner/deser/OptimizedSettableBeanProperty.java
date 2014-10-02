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
    private static final long serialVersionUID = -4221811783114701986L;

    /**
     * We will need to keep the original instance handy as
     * some calls are best just delegated
     */
    protected final SettableBeanProperty _originalSettable;
    
    protected final BeanPropertyMutator _propertyMutator;
    protected final int _optimizedIndex;

    /*
    /********************************************************************** 
    /* Life-cycle
    /********************************************************************** 
     */
    
    public OptimizedSettableBeanProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src);
        _originalSettable = src;
        _propertyMutator = mutator;
        _optimizedIndex = index;
    }

    protected OptimizedSettableBeanProperty(OptimizedSettableBeanProperty<T> src,
            JsonDeserializer<?> deser)
    {
        super(src, deser);
        _originalSettable = src;
        _propertyMutator = src._propertyMutator;
        _optimizedIndex = src._optimizedIndex;
    }

    protected OptimizedSettableBeanProperty(OptimizedSettableBeanProperty<T> src,
            PropertyName name)
    {
        super(src, name);
        _originalSettable = src;
        _propertyMutator = src._propertyMutator;
        _optimizedIndex = src._optimizedIndex;
    }
    
    public abstract T withMutator(BeanPropertyMutator mut);

    @Override
    public abstract T withValueDeserializer(JsonDeserializer<?> deser);

    
    /*
    /********************************************************************** 
    /* Overridden getters
    /********************************************************************** 
     */

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> ann) {
        return _originalSettable.getAnnotation(ann);
    }

    @Override
    public AnnotatedMember getMember() {
        return _originalSettable.getMember();
    }
    
    /*
    /********************************************************************** 
    /* Deserialization, regular
    /********************************************************************** 
     */
    
    @Override
    public abstract void deserializeAndSet(JsonParser jp, DeserializationContext ctxt,
            Object arg2) throws IOException, JsonProcessingException;

    @Override
    public abstract void set(Object bean, Object value) throws IOException;

    /*
    /********************************************************************** 
    /* Deserialization, builders
    /********************************************************************** 
     */
    
    /* !!! 19-Feb-2012, tatu:
     * 
     * We do not yet generate code for these methods: it would not be hugely
     * difficult to add them, but let's first see if anyone needs them...
     * (it is non-trivial, adds code etc, so not without cost).
     * 
     * That is: we'll use Reflection fallback for Builder-based deserialization,
     * so it will not be significantly faster.
     */

    @Override
    public abstract Object deserializeSetAndReturn(JsonParser jp,
            DeserializationContext ctxt, Object instance)
        throws IOException, JsonProcessingException;


    @Override
    public Object setAndReturn(Object instance, Object value) throws IOException
    {
        return _originalSettable.setAndReturn(instance, value);
    }

    /*
    /********************************************************************** 
    /* Helper methods
    /********************************************************************** 
     */

    protected final int _deserializeInt(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        if (jp.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            return jp.getIntValue();
        }
        return jp.getValueAsInt();
    }

    protected final long _deserializeLong(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        if (jp.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            return jp.getLongValue();
        }
        return jp.getValueAsLong();
    }

    protected final boolean _deserializeBoolean(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        if (jp.getCurrentToken() == JsonToken.VALUE_TRUE) {
            return true;
        }
        if (jp.getCurrentToken() == JsonToken.VALUE_FALSE) {
            return false;
        }
        return jp.getValueAsBoolean();
    }

    protected final String _deserializeString(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
            if (_nullProvider == null) {
                return null;
            }
            return (String) _nullProvider.nullValue(ctxt);
        }
        String text = jp.getValueAsString();
        if (text != null) {
            return text;
        }
        return _convertToString(jp, ctxt);
    }

    /**
     * Helper method for coercing JSON values other than Strings into
     * Java String value.
     */
    protected final String _convertToString(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        JsonToken curr = jp.getCurrentToken();
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
        if (curr.isScalarValue()) { // should have been handled earlier, but just in case...
            return jp.getText();
        }
        if (curr == JsonToken.VALUE_NULL) { // should this ever happen?
            return null;
        }
        // but not markers:
        throw ctxt.mappingException(String.class);
    }
}
