package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public final class StringFieldPropertyWriter
    extends OptimizedBeanPropertyWriter<StringFieldPropertyWriter>
{
    public StringFieldPropertyWriter(BeanPropertyWriter src, BeanPropertyAccessor acc, int index,
            JsonSerializer<Object> ser) {
        super(src, acc, index, ser);
    }

    @Override
    public BeanPropertyWriter withSerializer(JsonSerializer<Object> ser) {
        return new StringFieldPropertyWriter(this, _propertyAccessor, _propertyIndex, ser);
    }
    
    @Override
    public StringFieldPropertyWriter withAccessor(BeanPropertyAccessor acc) {
        if (acc == null) throw new IllegalArgumentException();
        return new StringFieldPropertyWriter(this, acc, _propertyIndex, _serializer);
    }

    /*
    /**********************************************************
    /* Overrides
    /**********************************************************
     */

    @Override
    public final void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception
    {
        if (broken) {
            fallbackWriter.serializeAsField(bean, jgen, prov);
            return;
        }
        try {
            String value = _propertyAccessor.stringField(bean, _propertyIndex);
            // Null (etc) handling; copied from super-class impl
            if (value == null) {
                if (!_suppressNulls) {
                    jgen.writeFieldName(_name);
                    prov.defaultSerializeNull(jgen);
                }
                return;
            }
            if (_suppressableValue != null) {
                if (MARKER_FOR_EMPTY == _suppressableValue) {
                    if (value.length() == 0) {
                        return;
                    }
                } else if (_suppressableValue.equals(value)) {
                    return;
                }
            }
            jgen.writeFieldName(_name);
            jgen.writeString(value);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            fallbackWriter.serializeAsField(bean, jgen, prov);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            fallbackWriter.serializeAsField(bean, jgen, prov);
        }
    }
}
