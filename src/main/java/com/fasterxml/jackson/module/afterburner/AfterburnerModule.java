package com.fasterxml.jackson.module.afterburner;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

import com.fasterxml.jackson.module.afterburner.ser.SerializerModifier;

public class AfterburnerModule extends SimpleModule
{
    // TODO: externalize
    private final static Version VERSION = new Version(1, 8, 0, null);
    
    public AfterburnerModule()
    {
        super("Afterburner", VERSION);
    }

    @Override
    public void setupModule(SetupContext context)
    {
        super.setupModule(context);
        context.addBeanSerializerModifier(new SerializerModifier());
    }
}

