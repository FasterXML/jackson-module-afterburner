package com.fasterxml.jackson.module.afterburner.ser;

import java.util.*;

import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.ser.*;

public class SerializerModifier extends BeanSerializerModifier
{
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
            BasicBeanDescription beanDesc, List<BeanPropertyWriter> beanProperties)
    {
        // !!! TODO
        return beanProperties;
    }
}
