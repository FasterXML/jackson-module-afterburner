package com.fasterxml.jackson.module.afterburner.ser;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.impl.PropertySerializerMap;

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
    public final void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov)
        throws Exception
    {
        Object value = _propertyAccessor.objectField(bean, _propertyIndex);
        // Null (etc) handling; copied from super-class impl
        if (value == null) {
            if (!_suppressNulls) {
                jgen.writeFieldName(_name);
                prov.defaultSerializeNull(jgen);
            }
            return;
        }
        if (_suppressableValue != null && _suppressableValue.equals(value)) {
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
        jgen.writeFieldName(_name);
        if (_typeSerializer == null) {
            ser.serialize(value, jgen, prov);
        } else {
            ser.serializeWithType(value, jgen, prov, _typeSerializer);
        }
    }
}
