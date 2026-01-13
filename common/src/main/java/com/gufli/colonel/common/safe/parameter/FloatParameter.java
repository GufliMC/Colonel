package com.gufli.colonel.common.safe.parameter;

import com.gufli.colonel.common.build.CommandContext;
import com.gufli.colonel.common.build.CommandParameter;

public class FloatParameter extends CommandParameter {

    private final float min;
    private final float max;

    public FloatParameter(String name, float min, float max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public FloatParameter(String name, float min) {
        this(name, min, Float.MAX_VALUE);
    }

    public FloatParameter(String name) {
        this(name, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public float min() {
        return min;
    }

    public float max() {
        return max;
    }

    //

    @Override
    public Object parse(CommandContext context, String input) {
        float value = Float.parseFloat(input);
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value out of range");
        }
        return value;
    }
}
