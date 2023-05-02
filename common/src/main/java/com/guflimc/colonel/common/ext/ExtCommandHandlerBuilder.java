package com.guflimc.colonel.common.ext;

import com.guflimc.colonel.common.dispatch.definition.ReadMode;
import com.guflimc.colonel.common.dispatch.tree.CommandHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ExtCommandHandlerBuilder {

    private final List<ExtCommandSourceMapper> mappers = new ArrayList<>();
    private final List<ExtCommandParameter> parameters = new ArrayList<>();

    private ExtCommandExecutor executor;
    private Predicate<Object> condition;

    //

    public ExtCommandHandlerBuilder parameter(@NotNull ExtCommandParameter parameter) {
        parameters.add(parameter);
        return this;
    }

    public ExtCommandHandlerBuilder parameter(@NotNull String name,
                                              @NotNull ReadMode readMode,
                                              @NotNull ExtCommandParameterParser parser,
                                              @NotNull ExtCommandParameterCompleter completer) {
        return parameter(ExtCommandParameter.of(name, readMode, parser, completer));
    }

    public ExtCommandHandlerBuilder parameter(@NotNull String name,
                                              @NotNull ReadMode readMode,
                                              @NotNull ExtCommandParameterParser parser) {
        return parameter(name, readMode, parser, (context, input) -> List.of());
    }

    //

    public ExtCommandHandlerBuilder string(@NotNull String name,
                                           @NotNull ExtCommandParameterParser parser) {
        return parameter(name, ReadMode.STRING, parser);
    }

    public ExtCommandHandlerBuilder string(@NotNull String name,
                                           @NotNull ExtCommandParameterParser parser,
                                           @NotNull ExtCommandParameterCompleter completer) {
        return parameter(name, ReadMode.STRING, parser, completer);
    }

    public ExtCommandHandlerBuilder greedy(@NotNull String name,
                                           @NotNull ExtCommandParameterParser parser) {
        return parameter(name, ReadMode.GREEDY, parser);
    }

    public ExtCommandHandlerBuilder greedy(@NotNull String name,
                                           @NotNull ExtCommandParameterParser parser,
                                           @NotNull ExtCommandParameterCompleter completer) {
        return parameter(name, ReadMode.GREEDY, parser, completer);
    }

    //

    public ExtCommandHandlerBuilder executor(ExtCommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public ExtCommandHandlerBuilder condition(Predicate<Object> condition) {
        this.condition = condition;
        return this;
    }

    //

    public ExtCommandHandlerBuilder source(ExtCommandSourceMapper mapper) {
        mappers.add(mapper);
        return this;
    }

    //

    public CommandHandler build() {
        ExtCommandParameter[] parameters = this.parameters.toArray(ExtCommandParameter[]::new);
        ExtCommandSourceMapper[] mappers = this.mappers.toArray(ExtCommandSourceMapper[]::new);

        return new ExtCommandHandler(parameters, executor, mappers, condition);
    }

}

