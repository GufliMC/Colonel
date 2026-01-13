package com.gufli.colonel.common.safe.parameter;

import com.gufli.colonel.common.build.CommandContext;
import com.gufli.colonel.common.build.CommandParameter;

public class StringParameter extends CommandParameter {

    public StringParameter(String name) {
        super(name);
    }

    //

    @Override
    public Object parse(CommandContext context, String input) {
        return input;
    }
}
