package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class StringMethodPropertyWriter 
    extends OptimizedBeanPropertyWriter<StringMethodPropertyWriter>
{
    public StringMethodPropertyWriter(BeanPropertyWriter src, BeanPropertyAccessor acc, int index,
            JsonSerializer<Object> ser) {
        super(src, acc, index, ser);
    }

    @Override
    public BeanPropertyWriter withSerializer(JsonSerializer<Object> ser) {
        return new StringMethodPropertyWriter(this, _propertyAccessor, _propertyIndex, ser);
    }
    
    @Override
    public StringMethodPropertyWriter withAccessor(BeanPropertyAccessor acc) {
        if (acc == null) throw new IllegalArgumentException();
        return new StringMethodPropertyWriter(this, acc, _propertyIndex, _serializer);
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
            String value = _propertyAccessor.stringGetter(bean, _propertyIndex);
            // Null (etc) handling; copied from super-class impl
            if (value == null) {
                if (!_suppressNulls) {
                    jgen.writeFieldName(_fastName);
                    prov.defaultSerializeNull(jgen);
                }
                return;
            }
            if (_suppressableValue != null) {
                if (MARKER_FOR_EMPTY == _suppressableValue) {
                    if (value.length() == 0) {
                        return;
                    }
                } else if (_suppressableValue.equals(value)) {
                    return;
                }
            }
            jgen.writeFieldName(_fastName);
            jgen.writeString(value);
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
                String value = _propertyAccessor.stringGetter(bean, _propertyIndex);
                // Null (etc) handling; copied from super-class impl
                if (value == null) {
                    if (_suppressNulls) {
                        serializeAsPlaceholder(bean, jgen, prov);
                    } else {
                        prov.defaultSerializeNull(jgen);
                    }
                    return;
                }
                if (_suppressableValue != null) {
                    if (MARKER_FOR_EMPTY == _suppressableValue) {
                        if (value.length() == 0) {
                            serializeAsPlaceholder(bean, jgen, prov);
                            return;
                        }
                    } else if (_suppressableValue.equals(value)) {
                        serializeAsPlaceholder(bean, jgen, prov);
                        return;
                    }
                }
                jgen.writeString(value);
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
