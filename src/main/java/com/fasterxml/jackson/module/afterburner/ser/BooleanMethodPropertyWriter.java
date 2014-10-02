package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public final class BooleanMethodPropertyWriter
    extends OptimizedBeanPropertyWriter<BooleanMethodPropertyWriter>
{
    private final boolean _suppressableSet;
    private final boolean _suppressableBoolean;

    public BooleanMethodPropertyWriter(BeanPropertyWriter src, BeanPropertyAccessor acc, int index,
            JsonSerializer<Object> ser) {
        super(src, acc, index, ser);

        if (MARKER_FOR_EMPTY == _suppressableValue) {
            _suppressableBoolean = false;
            _suppressableSet = true;
        } else if (_suppressableValue instanceof Boolean) {
            _suppressableBoolean = ((Boolean)_suppressableValue).booleanValue();
            _suppressableSet = true;
        } else {
            _suppressableBoolean = false;
            _suppressableSet = false;
        }
    }

    @Override
    public BeanPropertyWriter withSerializer(JsonSerializer<Object> ser) {
        return new BooleanMethodPropertyWriter(this, _propertyAccessor, _propertyIndex, ser);
    }
    
    @Override
    public BooleanMethodPropertyWriter withAccessor(BeanPropertyAccessor acc) {
        if (acc == null) throw new IllegalArgumentException();
        return new BooleanMethodPropertyWriter(this, acc, _propertyIndex, _serializer);
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
            boolean value = _propertyAccessor.booleanGetter(bean, _propertyIndex);
            if (!_suppressableSet || _suppressableBoolean != value) {
                jgen.writeFieldName(_name);
                jgen.writeBoolean(value);
            }
            return;
        } catch (IllegalAccessError e) {
            _reportProblem(bean, e);
        } catch (SecurityException e) {
            _reportProblem(bean, e);
        }
        fallbackWriter.serializeAsField(bean, jgen, prov);
    }

    @Override
    public final void serializeAsElement(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception
    {
        if (!broken) {
            try {
                boolean value = _propertyAccessor.booleanGetter(bean, _propertyIndex);
                if (!_suppressableSet || _suppressableBoolean != value) {
                    jgen.writeBoolean(value);
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
