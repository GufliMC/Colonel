package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

public class StringParameter extends ExtCommandParameter {

    public StringParameter(String name) {
        super(name);
    }

    //

    @Override
    public Argument parse(ExtCommandContext context, String input) {
        return Argument.success(input);
    }
}
