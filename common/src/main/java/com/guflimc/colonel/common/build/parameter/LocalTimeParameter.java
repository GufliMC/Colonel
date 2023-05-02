package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

import java.time.LocalTime;

public class LocalTimeParameter extends ExtCommandParameter {

    private final LocalTime min;
    private final LocalTime max;

    public LocalTimeParameter(String name, LocalTime min, LocalTime max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public LocalTimeParameter(String name, LocalTime min) {
        this(name, min, LocalTime.MIN);
    }

    public LocalTimeParameter(String name) {
        this(name, LocalTime.MAX);
    }

    public LocalTime min() {
        return min;
    }

    public LocalTime max() {
        return max;
    }

    //

    @Override
    public Argument parse(ExtCommandContext context, String input) {
        LocalTime value = LocalTime.parse(input);
        if (value.isBefore(min) || value.isAfter(max)) {
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Value out of range");
            });
        }
        return Argument.success(value);
    }
}
