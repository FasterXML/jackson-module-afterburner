package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;

public class ObjectFieldPropertyWriter
    extends OptimizedBeanPropertyWriter<ObjectFieldPropertyWriter>
{
    public ObjectFieldPropertyWriter(BeanPropertyWriter src, BeanPropertyAccessor acc, int index,
            JsonSerializer<Object> ser) {
        super(src, acc, index, ser);
    }

    @Override
    public BeanPropertyWriter withSerializer(JsonSerializer<Object> ser) {
        return new ObjectFieldPropertyWriter(this, _propertyAccessor, _propertyIndex, ser);
    }

    @Override
    public ObjectFieldPropertyWriter withAccessor(BeanPropertyAccessor acc) {
        if (acc == null) throw new IllegalArgumentException();
        return new ObjectFieldPropertyWriter(this, acc, _propertyIndex, _serializer);
    }

    /*
    /**********************************************************
    /* Overrides
    /**********************************************************
     */

    @Override
    public final void unsafeSerializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov)
        throws Exception
    {
        Object value = _propertyAccessor.objectField(bean, _propertyIndex);
        // Null (etc) handling; copied from super-class impl
        if (value == null) {
            if (_nullSerializer != null) {
                jgen.writeFieldName(_name);
                _nullSerializer.serialize(null, jgen, prov);
            } else if (!_suppressNulls) {
                jgen.writeFieldName(_name);
                prov.defaultSerializeNull(jgen);
            }
            return;
        }
        JsonSerializer<Object> ser = _serializer;
        if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap map = _dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
                ser = _findAndAddDynamic(map, cls, prov);
            }
        }
        if (_suppressableValue != null) {
            if (MARKER_FOR_EMPTY == _suppressableValue) {
                if (ser.isEmpty(value)) {
                    return;
                }
            } else if (_suppressableValue.equals(value)) {
                return;
            }
        }
        // !!! TODO: 01-Mar-2014, tatu: Fix in 2.4 to use the new method that allows
        //    both detection of tight self refs AND Object Ids.
        if (value == bean) {
            _handleSelfReference(bean, ser);
        }
        jgen.writeFieldName(_name);
        if (_typeSerializer == null) {
            ser.serialize(value, jgen, prov);
        } else {
            ser.serializeWithType(value, jgen, prov, _typeSerializer);
        }
    }
}
