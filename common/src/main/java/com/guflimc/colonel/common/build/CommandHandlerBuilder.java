package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.dispatch.definition.ReadMode;
import com.guflimc.colonel.common.dispatch.tree.CommandHandler;
import com.guflimc.colonel.common.ext.ExtCommandHandlerBuilder;
import com.guflimc.colonel.common.ext.ExtCommandParameterCompleter;
import com.guflimc.colonel.common.ext.ExtCommandParameterParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommandHandlerBuilder<S> {

    protected final Colonel<S> colonel;

    protected final List<String> paths = new ArrayList<>();
    protected final ExtCommandHandlerBuilder builder = new ExtCommandHandlerBuilder();

    public CommandHandlerBuilder(Colonel<S> colonel) {
        this.colonel = colonel;
    }

    //

    public CommandHandlerBuilder<S> path(String path) {
        this.paths.add(path);
        return this;
    }

    //

    protected CommandHandlerBuilder<S> parameter(@NotNull String name,
                                                 @NotNull ReadMode readMode,
                                                 @NotNull ExtCommandParameterParser parser,
                                                 @NotNull ExtCommandParameterCompleter completer) {
        builder.parameter(name, readMode, parser, completer);
        return this;
    }

    //

    public CommandParameterBuilder<S> parameter() {
        return new CommandParameterBuilder<>(this);
    }

    public CommandParameterBuilder<S> string() {
        return parameter().readString();
    }

    public CommandParameterBuilder<S> greedy() {
        return parameter().readGreedy();
    }

    //

    public CommandParameterBuilder<S> parameter(@NotNull String name) {
        return parameter().name(name);
    }

    public CommandParameterBuilder<S> string(@NotNull String name) {
        return parameter(name).readString();
    }

    public CommandParameterBuilder<S> greedy(@NotNull String name) {
        return parameter(name).readGreedy();
    }

    //

    public CommandParameterBuilder<S> parameter(@NotNull String name,
                                                @NotNull CommandParameterParser<S> parser) {
        return parameter(name).parser(parser);
    }

    public CommandParameterBuilder<S> string(@NotNull String name,
                                             @NotNull CommandParameterParser<S> parser) {
        return parameter(name, parser).readString();
    }

    public CommandParameterBuilder<S> greedy(@NotNull String name,
                                             @NotNull CommandParameterParser<S> parser) {
        return parameter(name, parser).readGreedy();
    }

    //

    public CommandParameterBuilder<S> parameter(@NotNull String name,
                                                @NotNull Function<String, Object> parser) {
        return parameter(name).parser(parser);
    }

    public CommandParameterBuilder<S> string(@NotNull String name,
                                             @NotNull Function<String, Object> parser) {
        return parameter(name, parser).readString();
    }

    public CommandParameterBuilder<S> greedy(@NotNull String name,
                                             @NotNull Function<String, Object> parser) {
        return parameter(name, parser).readGreedy();
    }

    //

    public CommandHandlerBuilder<S> executor(CommandExecutor<S> executor) {
        builder.executor(context -> executor.execute(new CommandContext<>(context)));
        return this;
    }

    public CommandHandlerBuilder<S> condition(Predicate<S> condition) {
        builder.condition(source -> condition.test((S) source));
        return this;
    }

    //

    public CommandHandlerBuilder<S> source(CommandSourceMapper<S> mapper) {
        builder.source(source -> mapper.map((S) source));
        return this;
    }

    public CommandHandlerBuilder<S> source(@NotNull Class<?> type,
                                           @Nullable String mapperName) {
        CommandSourceMapper<S> mapper;
        if (mapperName != null && !mapperName.isEmpty()) {
            mapper = colonel.registry().mapper(type, mapperName, false)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No mapper with name '%s' found for type %s.", mapperName, type.getName())));
        } else {
            mapper = colonel.registry().mapper(type)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No mapper for type %s found.", type.getName())));
        }
        return source(mapper);
    }

    public CommandHandlerBuilder<S> source(@NotNull Class<?> type) {
        return source(type, null);
    }

    //

    public void register() {
        if (paths.isEmpty()) {
            throw new IllegalStateException("There must be at least 1 path to register a handler");
        }

        CommandHandler handler = builder.build();
        paths.forEach(path -> colonel.register(path, handler));
    }

}

