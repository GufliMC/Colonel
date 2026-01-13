package com.gufli.colonel.common.safe.parameter;

import com.gufli.colonel.common.build.CommandContext;
import com.gufli.colonel.common.build.CommandParameter;

public class DoubleParameter extends CommandParameter {

    private final double min;
    private final double max;

    public DoubleParameter(String name, double min, double max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public DoubleParameter(String name, double min) {
        this(name, min, Double.MAX_VALUE);
    }

    public DoubleParameter(String name) {
        this(name, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    //

    @Override
    public Object parse(CommandContext context, String input) {
        double value = Double.parseDouble(input);
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value out of range");
        }
        return value;
    }
}
