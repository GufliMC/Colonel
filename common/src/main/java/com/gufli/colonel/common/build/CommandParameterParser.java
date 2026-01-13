package com.gufli.colonel.common.build;

@FunctionalInterface
public interface CommandParameterParser {

    Object parse(CommandContext context, String input) throws Throwable;

}
