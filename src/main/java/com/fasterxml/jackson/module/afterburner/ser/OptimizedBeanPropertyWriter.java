package com.fasterxml.jackson.module.afterburner.ser;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;

/**
 * Intermediate base class that is used for concrete
 * per-type implementations
 */
abstract class OptimizedBeanPropertyWriter<T extends OptimizedBeanPropertyWriter<T>>
    extends BeanPropertyWriter
{
    protected final BeanPropertyAccessor _propertyAccessor;
    protected final int _propertyIndex;
    
    protected OptimizedBeanPropertyWriter(BeanPropertyWriter src,
            BeanPropertyAccessor propertyAccessor, int propertyIndex,
            JsonSerializer<Object> ser)
    {
        super(src, ser);
        _propertyAccessor = propertyAccessor;
        _propertyIndex = propertyIndex;
    }
    
    public abstract T withAccessor(BeanPropertyAccessor acc);

    public abstract BeanPropertyWriter withSerializer(JsonSerializer<Object> ser);
    
    @Override
    public abstract void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov)
        throws Exception;
}