package com.fasterxml.jackson.module.afterburner.deser;

import org.codehaus.jackson.map.deser.impl.StdValueInstantiator;

/**
 * Base class for concrete bytecode-generated value instantiators.
 */
public abstract class OptimizedValueInstantiator
    extends StdValueInstantiator
{
    /**
     * Default constructor which is only used when creating
     * dummy instance to call factory method.
     */
    protected OptimizedValueInstantiator() {
        super(/*DeserializationConfig*/null, (Class<?>)null);
    }

    /**
     * Copy-constructor to use for creating actual optimized instances.
     */
    protected OptimizedValueInstantiator(StdValueInstantiator src) {
        super(src);
    }

    /**
     * Need to override this, now that we have installed default creator.
     */
    @Override
    public boolean canCreateUsingDefault() {
        return true;
    }
    
    protected abstract OptimizedValueInstantiator with(StdValueInstantiator src);
}
