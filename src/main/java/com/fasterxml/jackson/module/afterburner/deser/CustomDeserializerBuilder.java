package com.fasterxml.jackson.module.afterburner.deser;

import java.util.*;

import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializerBuilder;
import org.codehaus.jackson.map.deser.SettableBeanProperty;

public class CustomDeserializerBuilder extends BeanDeserializerBuilder
{
    public CustomDeserializerBuilder(BeanDeserializerBuilder base)
    {
        super(base);
    }

    public JsonDeserializer<?> build(BeanProperty forProperty)
    {
        JsonDeserializer<?> deser = (BeanDeserializer) super.build(forProperty);
        // only create custom one, if existing one is standard deserializer;
        if (deser.getClass() == BeanDeserializer.class) {
            BeanDeserializer beanDeser = (BeanDeserializer) deser;
            Iterator<SettableBeanProperty> it = getProperties();
            // also: only build custom one for non-empty beans:
            if (it.hasNext()) {
                // So let's find actual order of properties, necessary for optimal access
                ArrayList<SettableBeanProperty> props = new ArrayList<SettableBeanProperty>();
                do {
                    props.add(it.next());
                } while (it.hasNext());
                return new SuperSonicBeanDeserializer(beanDeser, props);
            }
        }
        return deser;
    }
}
