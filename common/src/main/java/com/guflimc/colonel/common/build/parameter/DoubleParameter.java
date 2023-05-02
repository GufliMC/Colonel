package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

public class DoubleParameter extends ExtCommandParameter {

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
    public Argument parse(ExtCommandContext context, String input) {
        double value = Double.parseDouble(input);
        if (value < min || value > max) {
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Value out of range");
            });
        }
        return Argument.success(value);
    }
}
