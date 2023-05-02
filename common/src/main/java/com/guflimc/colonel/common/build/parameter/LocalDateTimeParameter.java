package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

import java.time.LocalDateTime;

public class LocalDateTimeParameter extends ExtCommandParameter {

    private final LocalDateTime min;
    private final LocalDateTime max;

    public LocalDateTimeParameter(String name, LocalDateTime min, LocalDateTime max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public LocalDateTimeParameter(String name, LocalDateTime min) {
        this(name, min, LocalDateTime.MIN);
    }

    public LocalDateTimeParameter(String name) {
        this(name, LocalDateTime.MAX);
    }

    public LocalDateTime min() {
        return min;
    }

    public LocalDateTime max() {
        return max;
    }

    //

    @Override
    public Argument parse(ExtCommandContext context, String input) {
        LocalDateTime value = LocalDateTime.parse(input);
        if (value.isBefore(min) || value.isAfter(max)) {
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Value out of range");
            });
        }
        return Argument.success(value);
    }
}
