package com.gufli.colonel.common.safe.parameter;

import com.gufli.colonel.common.build.CommandContext;
import com.gufli.colonel.common.build.CommandParameter;

import java.time.LocalDate;

public class LocalDateParameter extends CommandParameter {

    private final LocalDate min;
    private final LocalDate max;

    public LocalDateParameter(String name, LocalDate min, LocalDate max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public LocalDateParameter(String name, LocalDate min) {
        this(name, min, LocalDate.MIN);
    }

    public LocalDateParameter(String name) {
        this(name, LocalDate.MAX);
    }

    public LocalDate min() {
        return min;
    }

    public LocalDate max() {
        return max;
    }

    //

    @Override
    public Object parse(CommandContext context, String input) {
        LocalDate value = LocalDate.parse(input);
        if (value.isBefore(min) || value.isAfter(max)) {
            throw new IllegalArgumentException("Value out of range");
        }
        return value;
    }
}
