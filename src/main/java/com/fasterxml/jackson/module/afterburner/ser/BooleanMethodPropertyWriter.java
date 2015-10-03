package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public final class BooleanMethodPropertyWriter
    extends OptimizedBeanPropertyWriter<BooleanMethodPropertyWriter>
{
    private static final long serialVersionUID = 1L;

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
    public final void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception
    {
        if (broken) {
            fallbackWriter.serializeAsField(bean, gen, prov);
            return;
        }
        boolean value;
        try {
            value = _propertyAccessor.booleanGetter(bean, _propertyIndex);
        } catch (Throwable t) {
            _handleProblem(bean, gen, prov, t, false);
            return;
        }
        if (!_suppressableSet || _suppressableBoolean != value) {
            gen.writeFieldName(_fastName);
            gen.writeBoolean(value);
        }
    }

    @Override
    public final void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception
    {
        if (broken) {
            fallbackWriter.serializeAsElement(bean, gen, prov);
            return;
        }            
        boolean value;
        try {
            value = _propertyAccessor.booleanGetter(bean, _propertyIndex);
        } catch (Throwable t) {
            _handleProblem(bean, gen, prov, t, true);
            return;
        }
        if (!_suppressableSet || _suppressableBoolean != value) {
            gen.writeBoolean(value);
        } else { // important: MUST output a placeholder
            serializeAsPlaceholder(bean, gen, prov);
        }
    }
}
