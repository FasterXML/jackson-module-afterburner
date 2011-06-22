package com.fasterxml.jackson.module.afterburner.ser;

import java.util.*;

import org.codehaus.jackson.map.ser.BeanPropertyWriter;

public class PropertyCollector
{
    private final ArrayList<IntMethodPropertyWriter> _intGetters = new ArrayList<IntMethodPropertyWriter>();
    
    public PropertyCollector() { }

    public IntMethodPropertyWriter addIntGetter(BeanPropertyWriter bpw) {
        return _add(_intGetters, new IntMethodPropertyWriter(bpw, null, _intGetters.size()));
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    private <T extends OptimizedBeanPropertyWriter<T>> T _add(List<T> list, T value) {
        list.add(value);
        return value;
    }
}
