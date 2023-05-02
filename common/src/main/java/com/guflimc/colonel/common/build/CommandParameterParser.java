package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.ext.Argument;

@FunctionalInterface
public interface CommandParameterParser<S> {

    Argument parse(CommandContext<S> context, String input);

}
