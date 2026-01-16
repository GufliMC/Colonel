package com.gufli.colonel.common.build;

import com.gufli.colonel.common.dispatch.definition.ReadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class CommandHandlerBuilder {

    private final List<CommandSourceMapper> mappers = new ArrayList<>();
    private final List<CommandParameter> parameters = new ArrayList<>();

    private final Map<String, Object> properties = new HashMap<>();

    private CommandExecutor executor;
    private Predicate<Object> condition;

    //

    public CommandHandlerBuilder parameter(@NotNull CommandParameter parameter) {
        parameters.add(parameter);
        return this;
    }

    public CommandHandlerBuilder parameter(@NotNull String name,
                                           @NotNull ReadMode readMode,
                                           @NotNull CommandParameterParser parser,
                                           @NotNull CommandParameterCompleter completer) {
        return parameter(CommandParameter.of(name, readMode, parser, completer));
    }

    public CommandHandlerBuilder parameter(@NotNull String name,
                                           @NotNull ReadMode readMode,
                                           @NotNull CommandParameterParser parser) {
        return parameter(name, readMode, parser, (context, input) -> List.of());
    }

    //

    public CommandHandlerBuilder string(@NotNull String name,
                                        @NotNull CommandParameterParser parser) {
        return parameter(name, ReadMode.STRING, parser);
    }

    public CommandHandlerBuilder string(@NotNull String name,
                                        @NotNull CommandParameterParser parser,
                                        @NotNull CommandParameterCompleter completer) {
        return parameter(name, ReadMode.STRING, parser, completer);
    }

    public CommandHandlerBuilder greedy(@NotNull String name,
                                        @NotNull CommandParameterParser parser) {
        return parameter(name, ReadMode.GREEDY, parser);
    }

    public CommandHandlerBuilder greedy(@NotNull String name,
                                        @NotNull CommandParameterParser parser,
                                        @NotNull CommandParameterCompleter completer) {
        return parameter(name, ReadMode.GREEDY, parser, completer);
    }

    //

    public CommandHandlerBuilder executor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public CommandHandlerBuilder condition(Predicate<Object> condition) {
        this.condition = condition;
        return this;
    }

    //

    public CommandHandlerBuilder source(CommandSourceMapper mapper) {
        mappers.add(mapper);
        return this;
    }

    //

    public CommandHandlerBuilder property(@NotNull String key, @NotNull Object value) {
        properties.put(key, value);
        return this;
    }

    //

    public com.gufli.colonel.common.dispatch.tree.CommandHandler build() {
        CommandParameter[] parameters = this.parameters.toArray(CommandParameter[]::new);
        CommandSourceMapper[] mappers = this.mappers.toArray(CommandSourceMapper[]::new);

        return new CommandHandler(parameters, executor, mappers, condition, properties);
    }

}

