package com.guflimc.colonel.common.command.syntax;

import com.guflimc.colonel.common.command.CommandContext;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandExecutor {

    void invoke(@NotNull CommandContext context);

}
