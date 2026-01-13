package com.gufli.colonel.common.safe.parameter;

import com.gufli.colonel.common.build.CommandContext;
import com.gufli.colonel.common.build.CommandParameter;

import java.time.LocalDateTime;

public class LocalDateTimeParameter extends CommandParameter {

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
    public Object parse(CommandContext context, String input) {
        LocalDateTime value = LocalDateTime.parse(input);
        if (value.isBefore(min) || value.isAfter(max)) {
            throw new IllegalArgumentException("Value out of range");
        }
        return value;
    }
}
