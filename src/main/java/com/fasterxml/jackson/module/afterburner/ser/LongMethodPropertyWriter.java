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
    public final void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception
    {
        if (broken) {
            fallbackWriter.serializeAsField(bean, jgen, prov);
            return;
        }
        try {
            long value = _propertyAccessor.longGetter(bean, _propertyIndex);
            if (!_suppressableLongSet || _suppressableLong != value) {
                jgen.writeFieldName(_fastName);
                jgen.writeNumber(value);
            }
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
            fallbackWriter.serializeAsField(bean, jgen, prov);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
            fallbackWriter.serializeAsField(bean, jgen, prov);
        }
    }

    @Override
    public final void serializeAsElement(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception
    {
        if (!broken) {
            try {
                long value = _propertyAccessor.longGetter(bean, _propertyIndex);
                if (!_suppressableLongSet || _suppressableLong != value) {
                    jgen.writeNumber(value);
                } else { // important: MUST output a placeholder
                    serializeAsPlaceholder(bean, jgen, prov);
                }
                return;
            } catch (IllegalAccessError e) {
                _reportProblem(bean, e);
            } catch (SecurityException e) {
                _reportProblem(bean, e);
            }
        }
        fallbackWriter.serializeAsElement(bean, jgen, prov);
    }
}
