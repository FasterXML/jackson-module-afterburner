package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public final class LongMethodPropertyWriter
    extends OptimizedBeanPropertyWriter<LongMethodPropertyWriter>
{
    private final long _suppressableLong;
    private final boolean _suppressableLongSet;

    public LongMethodPropertyWriter(BeanPropertyWriter src, BeanPropertyAccessor acc, int index,
            JsonSerializer<Object> ser) {
        super(src, acc, index, ser);

        if (_suppressableValue != null && _suppressableValue instanceof Long) {
            _suppressableLong = (Long)_suppressableValue;
            _suppressableLongSet = true;
        } else {
            _suppressableLong = 0;
            _suppressableLongSet = false;
        }
    }

    @Override
    public BeanPropertyWriter withSerializer(JsonSerializer<Object> ser) {
        return new LongMethodPropertyWriter(this, _propertyAccessor, _propertyIndex, ser);
    }

    @Override
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
    public void unsafeSerializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov)
        throws Exception
    {
        long value = _propertyAccessor.longGetter(bean, _propertyIndex);
        if (!_suppressableLongSet || _suppressableLong != value) {
            jgen.writeFieldName(_name);
            jgen.writeNumber(value);
        }
    }
}
