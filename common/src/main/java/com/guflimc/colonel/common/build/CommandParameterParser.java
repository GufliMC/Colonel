package com.guflimc.colonel.common.build;

@FunctionalInterface
public interface CommandParameterParser<S> {

    Argument parse(CommandContext<S> context, String value);

}
