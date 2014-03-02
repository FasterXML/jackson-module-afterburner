package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public final class LongFieldPropertyWriter
    extends OptimizedBeanPropertyWriter<LongFieldPropertyWriter>
{
    private final long _suppressableLong;
    private final boolean _suppressableLongSet;

    public LongFieldPropertyWriter(BeanPropertyWriter src, BeanPropertyAccessor acc, int index,
            JsonSerializer<Object> ser) {
        super(src, acc, index, ser);

        if (MARKER_FOR_EMPTY == _suppressableValue) {
            _suppressableLong = 0L;
            _suppressableLongSet = true;
        } else if (_suppressableValue instanceof Long) {
            _suppressableLong = (Long)_suppressableValue;
            _suppressableLongSet = true;
        } else {
            _suppressableLong = 0L;
            _suppressableLongSet = false;
        }
    }

    @Override
    public BeanPropertyWriter withSerializer(JsonSerializer<Object> ser) {
        return new LongFieldPropertyWriter(this, _propertyAccessor, _propertyIndex, ser);
    }

    @Override
    public LongFieldPropertyWriter withAccessor(BeanPropertyAccessor acc) {
        if (acc == null) throw new IllegalArgumentException();
        return new LongFieldPropertyWriter(this, acc, _propertyIndex, _serializer);
    }

    /*
    /**********************************************************
    /* Overrides
    /**********************************************************
     */

    @Override
    public final void unsafeSerializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov)
        throws Exception
    {
        long value = _propertyAccessor.longField(bean, _propertyIndex);
        if (!_suppressableLongSet || _suppressableLong != value) {
            jgen.writeFieldName(_name);
            jgen.writeNumber(value);
        }
    }
}
