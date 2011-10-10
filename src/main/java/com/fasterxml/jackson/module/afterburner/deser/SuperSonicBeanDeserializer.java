package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;
import java.util.*;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.io.SerializedString;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.deser.BeanDeserializer;
import org.codehaus.jackson.map.deser.SettableBeanProperty;

public class SuperSonicBeanDeserializer extends BeanDeserializer
{
    /**
     * Names of properties being deserialized, in ordered they are
     * expected to have been written (as per serialization settings);
     * used for speculative order-based optimizations
     */
    protected final SerializedString[] _orderedPropertyNames;

    /**
     * Properties matching names in {@link #_orderedPropertyNames}, 
     * assigned after resolution when property instances are finalized.
     */
    protected SettableBeanProperty[] _orderedProperties;
    
    /*
    /**********************************************************
    /* Life-cycle, construction, initialization
    /**********************************************************
     */
    
    public SuperSonicBeanDeserializer(BeanDeserializer src, List<SettableBeanProperty> props)
    {
        super(src, false);
        final int len = props.size();
        _orderedPropertyNames = new SerializedString[len];
        for (int i = 0; i < len; ++i) {
            _orderedPropertyNames[i] = new SerializedString(props.get(i).getName());
        }
    }

    protected SuperSonicBeanDeserializer(SuperSonicBeanDeserializer src, boolean ignoreAllUnknown)
    {
        super(src, ignoreAllUnknown);
        _orderedProperties = src._orderedProperties;
        _orderedPropertyNames = src._orderedPropertyNames;
    }
    
    @Override
    public JsonDeserializer<Object> unwrappingDeserializer()
    {
        return new SuperSonicBeanDeserializer(this, true);
    }

    /*
    /**********************************************************
    /* BenaDeserializer overrides
    /**********************************************************
     */

    /**
     * This method is overridden as we need to know expected order of
     * properties.
     */
    @Override
    public void resolve(DeserializationConfig config, DeserializerProvider provider)
        throws JsonMappingException
    {
        super.resolve(config, provider);
        /* Ok, now; need to find actual property instances to go with order
         * defined based on property names.
         */
        int len = _orderedPropertyNames.length;
        ArrayList<SettableBeanProperty> props = new ArrayList<SettableBeanProperty>(len);
        int i = 0;
        
        for (; i < len; ++i) {
            SettableBeanProperty prop = _beanProperties.find(_orderedPropertyNames[i].toString());
            if (prop == null) {
                break;
            }
            props.add(prop);
        }
        // should usually get at least one property; let's for now consider it an error if not
        // (may need to revisit in future)
        if (i == 0) {
            throw new IllegalStateException("Afterburner internal error: BeanDeserializer for "
                    +_beanType+" has no properties that match expected ordering -- can not create optimized deserializer");
        }
        _orderedProperties = props.toArray(new SettableBeanProperty[props.size()]);
    }

    // much of below is cut'n pasted from BeanSerializer
    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt, Object bean)
        throws IOException, JsonProcessingException
    {
        if (_injectables != null) {
            injectValues(ctxt, bean);
        }
        if (_unwrappedPropertyHandler != null) {
            return deserializeWithUnwrapped(jp, ctxt, bean);
        }
        if (_externalTypeIdHandler != null) {
            return deserializeWithExternalTypeId(jp, ctxt, bean);
        }
        JsonToken t = jp.getCurrentToken();
        SettableBeanProperty prop = _orderedProperties[0];
        // First: verify that first name is expected
        if (t == JsonToken.START_OBJECT) {
            if (!jp.nextFieldName(_orderedPropertyNames[0])) {
                return super.deserialize(jp,  ctxt, bean);
            }
            t = jp.nextToken();
        } else if (t != JsonToken.FIELD_NAME || !prop.getName().equals(jp.getCurrentName())) {
            return super.deserialize(jp,  ctxt, bean);
        }
        // and deserialize
        jp.nextToken();
        try {
            prop.deserializeAndSet(jp, ctxt, bean);
        } catch (Exception e) {
            wrapAndThrow(e, bean, prop.getName(), ctxt);
        }

        // then rest of properties
        for (int i = 1, len = _orderedProperties.length; i < len; ++i) {
            prop = _orderedProperties[i];
            if (!jp.nextFieldName(_orderedPropertyNames[i])) { // miss...
                if (jp.getCurrentToken() == JsonToken.END_OBJECT) {
                    break;
                }
                // we likely point to FIELD_NAME, so can just call parent impl
                return super.deserialize(jp, ctxt, bean);
            }
            jp.nextToken(); // skip field, returns value token
            try {
                prop.deserializeAndSet(jp, ctxt, bean);
            } catch (Exception e) {
                wrapAndThrow(e, bean, prop.getName(), ctxt);
            }
        }
        // also, need to ensure we get closing END_OBJECT...
        if (jp.nextToken() != JsonToken.END_OBJECT) {
            return super.deserialize(jp, ctxt, bean);
        }
        return bean;
    }

    // much of below is cut'n pasted from BeanSerializer
    @Override
    public Object deserializeFromObject(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        if (_nonStandardCreation) {
            if (_unwrappedPropertyHandler != null) {
                return deserializeWithUnwrapped(jp, ctxt);
            }
            if (_externalTypeIdHandler != null) {
                return deserializeWithExternalTypeId(jp, ctxt);
            }
            return deserializeFromObjectUsingNonDefault(jp, ctxt);
        }
        final Object bean = _valueInstantiator.createUsingDefault();
        if (_injectables != null) {
            injectValues(ctxt, bean);
        }
        JsonToken t = jp.getCurrentToken();
        SettableBeanProperty prop = _orderedProperties[0];
        // First: verify that first name is expected
        if (t == JsonToken.START_OBJECT) {
            if (!jp.nextFieldName(_orderedPropertyNames[0])) {
                return super.deserialize(jp,  ctxt, bean);
            }
            t = jp.nextToken();
        } else if (t != JsonToken.FIELD_NAME || !prop.getName().equals(jp.getCurrentName())) {
            return super.deserialize(jp,  ctxt, bean);
        }
        // and deserialize
        jp.nextToken();
        try {
            prop.deserializeAndSet(jp, ctxt, bean);
        } catch (Exception e) {
            wrapAndThrow(e, bean, prop.getName(), ctxt);
        }

        // then rest of properties
        for (int i = 1, len = _orderedProperties.length; i < len; ++i) {
            prop = _orderedProperties[i];
            if (!jp.nextFieldName(_orderedPropertyNames[i])) { // miss...
                if (jp.getCurrentToken() == JsonToken.END_OBJECT) {
                    break;
                }
                // we likely point to FIELD_NAME, so can just call parent impl
                return super.deserialize(jp, ctxt, bean);
            }
            jp.nextToken(); // skip field, returns value token
            try {
                prop.deserializeAndSet(jp, ctxt, bean);
            } catch (Exception e) {
                wrapAndThrow(e, bean, prop.getName(), ctxt);
            }
        }
        // also, need to ensure we get closing END_OBJECT...
        if (jp.nextToken() != JsonToken.END_OBJECT) {
            return super.deserialize(jp, ctxt, bean);
        }
        return bean;
    }
}
