package com.fasterxml.jackson.module.afterburner.bug48;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;

/**
 * @author Joost van de Wijgerd
 */
public final class TestObjectWithJsonSerialize {
    @JsonSerialize(using = ToStringSerializer.class)
    private final BigDecimal amount;

    @JsonCreator
    public TestObjectWithJsonSerialize(@JsonProperty("amount") BigDecimal amount) {
        this.amount = amount;
    }

    @JsonSerialize(using = ToStringSerializer.class) @JsonProperty("amount")
    public BigDecimal getAmount() {
        return amount;
    }
}
