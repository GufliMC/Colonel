package com.gufli.colonel.common.safe.parameter;

import com.gufli.colonel.common.build.CommandContext;
import com.gufli.colonel.common.build.CommandParameter;

public class LongParameter extends CommandParameter {

    private final Long min;
    private final Long max;

    public LongParameter(String name, Long min, Long max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public LongParameter(String name, Long min) {
        this(name, min, Long.MAX_VALUE);
    }

    public LongParameter(String name) {
        this(name, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public Long min() {
        return min;
    }

    public Long max() {
        return max;
    }

    //

    @Override
    public Object parse(CommandContext context, String input) {
        Long value = Long.parseLong(input);
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value out of range");
        }
        return value;
    }
}
