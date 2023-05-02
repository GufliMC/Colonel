package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

public class ShortParameter extends ExtCommandParameter {

    private final short min;
    private final short max;

    public ShortParameter(String name, short min, short max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public ShortParameter(String name, short min) {
        this(name, min, Short.MAX_VALUE);
    }

    public ShortParameter(String name) {
        this(name, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    public short min() {
        return min;
    }

    public short max() {
        return max;
    }

    //

    @Override
    public Argument parse(ExtCommandContext context, String input) {
        short value = Short.parseShort(input);
        if (value < min || value > max) {
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Value out of range");
            });
        }
        return Argument.success(value);
    }
}
