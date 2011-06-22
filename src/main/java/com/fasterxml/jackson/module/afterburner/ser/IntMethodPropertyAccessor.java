package com.fasterxml.jackson.module.afterburner.ser;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;

public final class IntMethodPropertyAccessor
    extends OptimizedBeanPropertyWriter<IntMethodPropertyAccessor>
{
    public IntMethodPropertyAccessor(BeanPropertyWriter src, BeanPropertyAccessor acc, int index) {
        super(src, null, index);
    }

    public IntMethodPropertyAccessor withAccessor(BeanPropertyAccessor acc) {
        return new IntMethodPropertyAccessor(this, acc, _propertyIndex);
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
