package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

import java.time.LocalDate;

public class LocalDateParameter extends ExtCommandParameter {

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
    public Argument parse(ExtCommandContext context, String input) {
        LocalDate value = LocalDate.parse(input);
        if (value.isBefore(min) || value.isAfter(max)) {
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Value out of range");
            });
        }
        return Argument.success(value);
    }
}
