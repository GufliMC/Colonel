package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

public class FloatParameter extends ExtCommandParameter {

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
    public Argument parse(ExtCommandContext context, String input) {
        float value = Float.parseFloat(input);
        if (value < min || value > max) {
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Value out of range");
            });
        }
        return Argument.success(value);
    }
}
