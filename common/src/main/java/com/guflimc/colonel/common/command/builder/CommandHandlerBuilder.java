package com.guflimc.colonel.common.command.builder;

import com.guflimc.colonel.common.command.CommandContext;
import com.guflimc.colonel.common.command.CommandDispatcherContext;
import com.guflimc.colonel.common.command.handler.CommandHandler;
import com.guflimc.colonel.common.command.handler.CommandParameter;
import com.guflimc.colonel.common.command.handler.CommandParameterParser;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CommandHandlerBuilder {

    private final CommandDispatcherContext context;

    private String[] literals;
    private Consumer<CommandContext> executor;

    private final List<CommandParameter<?>> parameters = new ArrayList<>();

    private Predicate<CommandContext> condition;

    public static CommandHandlerBuilder of(@NotNull CommandDispatcherContext context) {
        return new CommandHandlerBuilder(context);
    }

    private CommandHandlerBuilder(@NotNull CommandDispatcherContext context) {
        this.context = context;
    }

    //

    public CommandHandlerBuilder withLiterals(@NotNull String literals) {
        this.literals = literals.split(Pattern.quote(" "));
        return this;
    }

    public CommandHandlerBuilder withExecutor(@NotNull Consumer<CommandContext> executor) {
        this.executor = executor;
        return this;
    }

    public <T> CommandHandlerBuilder withParameter(@NotNull String name, @NotNull Class<T> type, @NotNull CommandParameterParser<T> parser, @NotNull Collection<String> dependencies) {
        Set<CommandParameter<?>> depends = dependencies.stream()
                .map(str -> parameters.stream().filter(p -> p.name().equals(str)).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        parameters.add(new CommandParameter<>(name, type, depends) {
            @Override
            public T parse(CommandContext context, String input) {
                return parser.parse(context, input);
            }
        });
        return this;
    }

    public <T> CommandHandlerBuilder withParameter(@NotNull String name, @NotNull Class<T> type, @NotNull CommandParameterParser<T> parser) {
        return withParameter(name, type, parser, Collections.emptySet());
    }

    public <T> CommandHandlerBuilder withParameter(@NotNull String name, @NotNull Class<T> type, @NotNull String parserName) {
        CommandDispatcherContext.ParameterParser<T> parser = context.parameterParser(type, parserName);
        return withParameter(name, type, parser.parser(), parser.dependencies());
    }

    public <T> CommandHandlerBuilder withParameter(@NotNull String name, @NotNull Class<T> type) {
        CommandDispatcherContext.ParameterParser<T> parser = context.parameterParser(type);
        return withParameter(name, type, parser.parser(), parser.dependencies());
    }

    //

    public CommandHandler build() {
        return new CommandHandler(literals, parameters.toArray(new CommandParameter[0])) {
            @Override
            public void invoke(CommandContext context) {
                executor.accept(context);
            }
        };
    }
}
