package com.guflimc.colonel.common.command.builder;

import com.guflimc.colonel.common.command.CommandDispatcherContext;
import com.guflimc.colonel.common.command.syntax.CommandParameter;
import com.guflimc.colonel.common.command.syntax.CommandParameterType;
import com.guflimc.colonel.common.command.syntax.CommandSyntax;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class CommandSyntaxBuilder {

    private final CommandDispatcherContext context;

    private String[] literals;

    private final List<CommandParameter<?>> parameters = new ArrayList<>();

    public static CommandSyntaxBuilder of(@NotNull CommandDispatcherContext context) {
        return new CommandSyntaxBuilder(context);
    }

    private CommandSyntaxBuilder(@NotNull CommandDispatcherContext context) {
        this.context = context;
    }

    //

    public CommandSyntaxBuilder withLiterals(@NotNull String literals) {
        this.literals = literals.split(Pattern.quote(" "));
        return this;
    }

    public <T> CommandSyntaxBuilder withParameter(@NotNull String name, @NotNull CommandParameterType<T> type) {
        parameters.add(new CommandParameter<>(name, type));
        return this;
    }

    public <T> CommandSyntaxBuilder withParameter(@NotNull String name, @NotNull Class<T> type, @NotNull String typeName) {
        return withParameter(name, context.parameterType(type, typeName));
    }

    public <T> CommandSyntaxBuilder withParameter(@NotNull String name, @NotNull Class<T> type) {
        return withParameter(name, context.parameterType(type));
    }

    //

    public CommandSyntax build() {
        return new CommandSyntax(literals, parameters.toArray(new CommandParameter[0]));
    }
}
