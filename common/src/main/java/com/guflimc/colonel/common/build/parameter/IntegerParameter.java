package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

public class IntegerParameter extends ExtCommandParameter {

    private final Integer min;
    private final Integer max;

    public IntegerParameter(String name, Integer min, Integer max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public IntegerParameter(String name, Integer min) {
        this(name, min, Integer.MAX_VALUE);
    }

    public IntegerParameter(String name) {
        this(name, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public Integer min() {
        return min;
    }

    public Integer max() {
        return max;
    }

    //

    @Override
    public Argument parse(ExtCommandContext context, String input) {
        Integer value = Integer.parseInt(input);
        if (value < min || value > max) {
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Value out of range");
            });
        }
        return Argument.success(value);
    }
}
