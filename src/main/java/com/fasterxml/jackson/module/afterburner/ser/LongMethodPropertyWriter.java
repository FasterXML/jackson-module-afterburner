package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public final class LongMethodPropertyWriter
    extends OptimizedBeanPropertyWriter<LongMethodPropertyWriter>
{
    public LongMethodPropertyWriter(BeanPropertyWriter src, BeanPropertyAccessor acc, int index,
            JsonSerializer<Object> ser) {
        super(src, acc, index, ser);
    }
    
    @Override
    public BeanPropertyWriter withSerializer(JsonSerializer<Object> ser) {
        return new LongMethodPropertyWriter(this, _propertyAccessor, _propertyIndex, ser);
    }
    
    public LongMethodPropertyWriter withAccessor(BeanPropertyAccessor acc) {
        if (acc == null) throw new IllegalArgumentException();
        return new LongMethodPropertyWriter(this, acc, _propertyIndex, _serializer);
    }
    
    /*
    /**********************************************************
    /* Overrides
    /**********************************************************
     */
    
    @Override
    public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov)
        throws Exception
    {
        jgen.writeFieldName(_name);
        jgen.writeNumber(_propertyAccessor.longGetter(bean, _propertyIndex));
    }
}
