package com.guflimc.colonel.common.command.builder;

import com.guflimc.colonel.common.command.Command;
import com.guflimc.colonel.common.command.CommandContext;
import com.guflimc.colonel.common.command.CommandDispatcherContext;
import com.guflimc.colonel.common.command.syntax.CommandSyntax;
import org.jetbrains.annotations.NotNull;

public final class CommandContextBuilder {

    private final CommandDispatcherContext context;

    private Command command;
    private CommandSyntax handler;

    public static CommandContextBuilder of(@NotNull CommandDispatcherContext context) {
        return new CommandContextBuilder(context);
    }

    private CommandContextBuilder(@NotNull CommandDispatcherContext context) {
        this.context = context;
    }

    //

    public CommandContextBuilder withCommand(@NotNull Command command) {
        this.command = command;
        return this;
    }

    public CommandContextBuilder withSyntax(@NotNull CommandSyntax handler) {
        this.handler = handler;
        return this;
    }

    //

    public CommandContext build() {
        return new CommandContext(command, handler) {

            @Override
            public <T> T source(Class<T> type) {
                return context.sourceParser(type).parse(this);
            }

            @Override
            public <T> T source(String providerName) {
                return (T) context.sourceParser(providerName).parse(this);
            }

        };
    }

}
