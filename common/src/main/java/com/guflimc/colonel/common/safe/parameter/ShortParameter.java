package com.guflimc.colonel.common.safe.parameter;

import com.guflimc.colonel.common.build.CommandContext;
import com.guflimc.colonel.common.build.CommandParameter;

public class ShortParameter extends CommandParameter {

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
    public Object parse(CommandContext context, String input) {
        short value = Short.parseShort(input);
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value out of range");
        }
        return value;
    }
}
