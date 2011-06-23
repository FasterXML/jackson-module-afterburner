package com.fasterxml.jackson.module.afterburner.ser;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;

public final class IntFieldPropertyWriter
    extends OptimizedBeanPropertyWriter<IntFieldPropertyWriter>
{
    public IntFieldPropertyWriter(BeanPropertyWriter src, BeanPropertyAccessor acc, int index,
            JsonSerializer<Object> ser) {
        super(src, acc, index, ser);
    }

    @Override
    public BeanPropertyWriter withSerializer(JsonSerializer<Object> ser) {
        return new IntMethodPropertyWriter(this, _propertyAccessor, _propertyIndex, ser);
    }
    
    public IntFieldPropertyWriter withAccessor(BeanPropertyAccessor acc) {
        if (acc == null) throw new IllegalArgumentException();
        return new IntFieldPropertyWriter(this, acc, _propertyIndex, _serializer);
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
        jgen.writeFieldName(_name);
        jgen.writeNumber(_propertyAccessor.intField(bean, _propertyIndex));
    }
}
