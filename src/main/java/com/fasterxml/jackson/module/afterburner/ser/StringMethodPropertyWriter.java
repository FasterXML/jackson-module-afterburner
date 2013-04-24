package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class StringMethodPropertyWriter 
    extends OptimizedBeanPropertyWriter<StringMethodPropertyWriter>
{
    public StringMethodPropertyWriter(BeanPropertyWriter src, BeanPropertyAccessor acc, int index,
            JsonSerializer<Object> ser) {
        super(src, acc, index, ser);
    }

    @Override
    public BeanPropertyWriter withSerializer(JsonSerializer<Object> ser) {
        return new StringMethodPropertyWriter(this, _propertyAccessor, _propertyIndex, ser);
    }
    
    @Override
    public StringMethodPropertyWriter withAccessor(BeanPropertyAccessor acc) {
        if (acc == null) throw new IllegalArgumentException();
        return new StringMethodPropertyWriter(this, acc, _propertyIndex, _serializer);
    }

    /*
    /**********************************************************
    /* Overrides
    /**********************************************************
     */

    @Override
    public void unsafeSerializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov)
        throws Exception
    {
        String value = _propertyAccessor.stringGetter(bean, _propertyIndex);
        // Null (etc) handling; copied from super-class impl
        if (value == null) {
            if (!_suppressNulls) {
                jgen.writeFieldName(_name);
                prov.defaultSerializeNull(jgen);
            }
            return;
        }
        jgen.writeFieldName(_name);
        jgen.writeString(value);
    }
}
