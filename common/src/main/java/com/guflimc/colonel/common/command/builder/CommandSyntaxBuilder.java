package com.guflimc.colonel.common.command.builder;

import com.guflimc.colonel.common.registry.TypeRegistryContainer;
import com.guflimc.colonel.common.command.syntax.CommandParameter;
import com.guflimc.colonel.common.command.syntax.CommandParameterType;
import com.guflimc.colonel.common.command.syntax.CommandSyntax;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public final class CommandSyntaxBuilder {

    private final TypeRegistryContainer registry;

    private String[] literals;

    private final List<CommandParameter<?>> parameters = new ArrayList<>();

    public static CommandSyntaxBuilder with(@NotNull TypeRegistryContainer registry) {
        return new CommandSyntaxBuilder(registry);
    }

    private CommandSyntaxBuilder(@NotNull TypeRegistryContainer registry) {
        this.registry = registry;
    }

    //

    public CommandSyntaxBuilder withLiterals(@NotNull String literals) {
        this.literals = literals.split(Pattern.quote(" "));
        return this;
    }

    public <T> CommandSyntaxBuilder withParameter(@NotNull CommandParameter<?> parameter) {
        parameters.add(parameter);
        return this;
    }

    public <T> CommandSyntaxBuilder withParameter(@NotNull String name, @NotNull CommandParameterType<T> type) {
        return withParameter(new CommandParameter<>(name, type));
    }

    public <T> CommandSyntaxBuilder withParameter(@NotNull String name, @NotNull Class<T> type, @NotNull String typeName) {
        return withParameter(name, registry.parameterType(type, typeName));
    }

    public <T> CommandSyntaxBuilder withParameter(@NotNull String name, @NotNull Class<T> type) {
        return withParameter(name, registry.parameterType(type));
    }

    public <T> CommandSyntaxBuilder withParameters(@NotNull Collection<CommandParameter<?>> parameters) {
        parameters.forEach(this::withParameter);
        return this;
    }

    public <T> CommandSyntaxBuilder withParameters(@NotNull CommandParameter<?>... parameters) {
        return withParameters(Arrays.asList(parameters));
    }

    //

    public CommandSyntax build() {
        return new CommandSyntax(literals, parameters.toArray(new CommandParameter[0]));
    }
}
