package com.guflimc.colonel.common.builder;

import com.guflimc.colonel.common.command.syntax.CommandExecutor;
import com.guflimc.colonel.common.command.syntax.CommandParameter;
import com.guflimc.colonel.common.command.syntax.CommandSyntax;

import java.util.Arrays;

public final class CommandMethod<S> {

    private final String[][] literals;
    private final CommandMethodParameter<?>[] parameters;

    private CommandExecutor executor;

    public CommandMethod(String[][] literals, CommandMethodParameter<?>[] parameters) {
        this.literals = literals;
        this.parameters = parameters;
    }

    public String[][] literals() {
        return literals;
    }

    public CommandMethodParameter<?>[] parameters() {
        return parameters;
    }

    public CommandExecutor executor() {
        return executor;
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public CommandSyntax[] build() {
        CommandParameter<?>[] parameters = Arrays.stream(this.parameters)
                .map(CommandMethodParameter::build)
                .toArray(CommandParameter<?>[]::new);

        CommandSyntax[] syntaxes = new CommandSyntax[literals.length];
        for (int i = 0; i < literals.length; i++) {
            syntaxes[i] = new CommandSyntax(literals[i], parameters);
        }

        return syntaxes;
    }

}
