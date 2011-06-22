package com.fasterxml.jackson.module.afterburner.ser;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;

public final class IntMethodPropertyWriter
    extends OptimizedBeanPropertyWriter<IntMethodPropertyWriter>
{
    public IntMethodPropertyWriter(BeanPropertyWriter src, BeanPropertyAccessor acc, int index) {
        super(src, null, index);
    }

    public IntMethodPropertyWriter withAccessor(BeanPropertyAccessor acc) {
        return new IntMethodPropertyWriter(this, acc, _propertyIndex);
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
        jgen.writeNumber(_propertyAccessor.intGetter(bean, _propertyIndex));
    }
}
