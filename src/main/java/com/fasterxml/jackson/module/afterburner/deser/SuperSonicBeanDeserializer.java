package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.*;
import com.fasterxml.jackson.databind.util.NameTransformer;

public final class SuperSonicBeanDeserializer extends BeanDeserializer
{
    private static final long serialVersionUID = -8468272764223072933L;

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
        super(src);
        final int len = props.size();
        _orderedPropertyNames = new SerializedString[len];
        for (int i = 0; i < len; ++i) {
            _orderedPropertyNames[i] = new SerializedString(props.get(i).getName());
        }
    }

    protected SuperSonicBeanDeserializer(SuperSonicBeanDeserializer src, NameTransformer unwrapper)
    {
        super(src, unwrapper);
        _orderedProperties = src._orderedProperties;
        _orderedPropertyNames = src._orderedPropertyNames;
    }
    
    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
        return new SuperSonicBeanDeserializer(this, unwrapper);
    }

    // // Others, let's just leave as is; will not be optimized?
    
    //public BeanDeserializer withObjectIdReader(ObjectIdReader oir) {

    //public BeanDeserializer withIgnorableProperties(HashSet<String> ignorableProps)
    
    //protected BeanDeserializerBase asArrayDeserializer()
    
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
    public void resolve(DeserializationContext ctxt)
        throws JsonMappingException
    {
        super.resolve(ctxt);
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

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        if (!_vanillaProcessing || _objectIdReader != null) {
            // should we ever get here? Just in case
            return super.deserialize(jp, ctxt);
        }
        JsonToken t = jp.getCurrentToken();
        // common case first:
        if (t != JsonToken.START_OBJECT) {
            return _deserializeOther(jp, ctxt, t);
        }
        t = jp.nextToken();
        // Inlined version of:
        return deserializeFromObject(jp, ctxt);
    }
    
    // much of below is cut'n pasted from BeanSerializer
    @Override
    public final Object deserialize(JsonParser jp, DeserializationContext ctxt, Object bean)
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
                    return bean;
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
    public final Object deserializeFromObject(JsonParser jp, DeserializationContext ctxt)
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
        final Object bean = _valueInstantiator.createUsingDefault(ctxt);
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
                    return bean;
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
