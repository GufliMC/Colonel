package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

public class ByteParameter extends ExtCommandParameter {

    private final byte min;
    private final byte max;

    public ByteParameter(String name, byte min, byte max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public ByteParameter(String name, byte min) {
        this(name, min, Byte.MAX_VALUE);
    }

    public ByteParameter(String name) {
        this(name, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    public byte min() {
        return min;
    }

    public byte max() {
        return max;
    }

    //

    @Override
    public Argument parse(ExtCommandContext context, String input) {
        byte value = Byte.parseByte(input);
        if (value < min || value > max) {
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Value out of range");
            });
        }
        return Argument.success(value);
    }
}
