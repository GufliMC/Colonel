package com.guflimc.colonel.common.build;

@FunctionalInterface
public interface CommandParameterParser {

    Object parse(CommandContext context, String input);

}
