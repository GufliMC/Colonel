package com.gufli.colonel.common.safe.parameter;

import com.gufli.colonel.common.build.CommandContext;
import com.gufli.colonel.common.build.CommandParameter;

public class IntegerParameter extends CommandParameter {

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
    public Object parse(CommandContext context, String input) {
        Integer value = Integer.parseInt(input);
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value out of range");
        }
        return value;
    }
}
