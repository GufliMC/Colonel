package com.gufli.colonel.common.safe.parameter;

import com.gufli.colonel.common.build.CommandContext;
import com.gufli.colonel.common.build.CommandParameter;

public class ByteParameter extends CommandParameter {

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
    public Object parse(CommandContext context, String input) {
        byte value = Byte.parseByte(input);
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value out of range");
        }
        return value;
    }
}
