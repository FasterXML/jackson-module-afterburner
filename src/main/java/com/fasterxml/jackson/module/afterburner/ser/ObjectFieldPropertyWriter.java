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
    public final void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception
    {
        if (broken) {
            fallbackWriter.serializeAsField(bean, gen, prov);
            return;
        }
        Object value;
        try {
            value = _propertyAccessor.objectField(bean, _propertyIndex);
        } catch (Throwable t) {
            _handleProblem(bean, gen, prov, t, false);
            return;
        }
        // Null (etc) handling; copied from super-class impl
        if (value == null) {
            if (_nullSerializer != null) {
                gen.writeFieldName(_fastName);
                _nullSerializer.serialize(null, gen, prov);
            } else if (!_suppressNulls) {
                gen.writeFieldName(_fastName);
                prov.defaultSerializeNull(gen);
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
        if (value == bean) {
            _handleSelfReference(bean, gen, prov, ser);
        }
        gen.writeFieldName(_fastName);
        if (_typeSerializer == null) {
            ser.serialize(value, gen, prov);
        } else {
            ser.serializeWithType(value, gen, prov, _typeSerializer);
        }
    }

    @Override
    public final void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception
    {
        if (broken) {
            fallbackWriter.serializeAsElement(bean, gen, prov);
            return;
        }
        Object value;
        try {
            value = _propertyAccessor.objectField(bean, _propertyIndex);
        } catch (Throwable t) {
            _handleProblem(bean, gen, prov, t, true);
            return;
        }
        if (value == null) {
            if (_nullSerializer != null) {
                _nullSerializer.serialize(null, gen, prov);
            } else if (_suppressNulls) {
                serializeAsPlaceholder(bean, gen, prov);
            } else {
                prov.defaultSerializeNull(gen);
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
                    serializeAsPlaceholder(bean, gen, prov);
                    return;
                }
            } else if (_suppressableValue.equals(value)) {
                serializeAsPlaceholder(bean, gen, prov);
                return;
            }
        }
        if (value == bean) {
            _handleSelfReference(bean, gen, prov, ser);
        }
        if (_typeSerializer == null) {
            ser.serialize(value, gen, prov);
        } else {
            ser.serializeWithType(value, gen, prov, _typeSerializer);
        }
    }
}
