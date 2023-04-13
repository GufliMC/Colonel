package com.guflimc.colonel.common.command.syntax;

import com.guflimc.colonel.common.command.CommandContext;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandParameterParser<T> {

    T parse(@NotNull CommandContext context, @NotNull String input);

}
