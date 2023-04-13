package com.guflimc.colonel.common.command.handler;

import com.guflimc.colonel.common.command.CommandContext;

@FunctionalInterface
public interface CommandParameterParser<T> {

    T parse(CommandContext context, String input);

}
