package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

/**
 * Intermediate base class that is used for concrete
 * per-type implementations
 */
abstract class OptimizedBeanPropertyWriter<T extends OptimizedBeanPropertyWriter<T>>
    extends BeanPropertyWriter
{
    protected final BeanPropertyAccessor _propertyAccessor;
    protected final int _propertyIndex;

    private final BeanPropertyWriter fallbackWriter;
    // Not volatile to prevent overhead, worst case is we trip the exception a few extra times
    private boolean broken = false;

    protected OptimizedBeanPropertyWriter(BeanPropertyWriter src,
            BeanPropertyAccessor propertyAccessor, int propertyIndex,
            JsonSerializer<Object> ser)
    {
        super(src);
        this.fallbackWriter = unwrapFallbackWriter(src);
        _serializer = ser; // from base class
        _propertyAccessor = propertyAccessor;
        _propertyIndex = propertyIndex;
    }

    private BeanPropertyWriter unwrapFallbackWriter(BeanPropertyWriter srcIn)
    {
        while (srcIn instanceof OptimizedBeanPropertyWriter) {
            srcIn = ((OptimizedBeanPropertyWriter<?>)srcIn).fallbackWriter;
        }
        return srcIn;
    }

    public abstract T withAccessor(BeanPropertyAccessor acc);

    public abstract BeanPropertyWriter withSerializer(JsonSerializer<Object> ser);

    @Override
    public final void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov)
    throws Exception
    {
        if (broken) {
            fallbackWriter.serializeAsField(bean, jgen, prov);
            return;
        }
        try {
            unsafeSerializeAsField(bean, jgen, prov);
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            fallbackWriter.serializeAsField(bean, jgen, prov);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            fallbackWriter.serializeAsField(bean, jgen, prov);
        }
    }

    public abstract void unsafeSerializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov)
            throws Exception;

    protected void _reportProblem(Object bean, Throwable e)
    {
        broken = true;
        System.err.format("Disabling Afterburner serialization for type %s, field #%d, due to access error (type %s, message=%s)%n",
                bean.getClass(), _propertyIndex,
                e.getClass().getName(), e.getMessage());
        e.printStackTrace(); // TODO
    }

}
