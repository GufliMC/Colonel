package com.guflimc.colonel.common.safe.parameter;

import com.guflimc.colonel.common.build.CommandContext;
import com.guflimc.colonel.common.build.CommandParameter;

import java.time.LocalTime;

public class LocalTimeParameter extends CommandParameter {

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
    public Object parse(CommandContext context, String input) {
        LocalTime value = LocalTime.parse(input);
        if (value.isBefore(min) || value.isAfter(max)) {
            throw new IllegalArgumentException("Value out of range");
        }
        return value;
    }
}
