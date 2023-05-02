package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

public class LongParameter extends ExtCommandParameter {

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
    public Argument parse(ExtCommandContext context, String input) {
        Long value = Long.parseLong(input);
        if (value < min || value > max) {
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Value out of range");
            });
        }
        return Argument.success(value);
    }
}
