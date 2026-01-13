package com.gufli.colonel.common.safe;

import com.gufli.colonel.common.Colonel;
import com.gufli.colonel.common.dispatch.definition.ReadMode;
import com.gufli.colonel.common.dispatch.tree.CommandHandler;
import com.gufli.colonel.common.build.CommandHandlerBuilder;
import com.gufli.colonel.common.build.CommandParameterCompleter;
import com.gufli.colonel.common.build.CommandParameterParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class SafeCommandHandlerBuilder<S> {

    protected final Colonel<S> colonel;

    protected final List<String> paths = new ArrayList<>();
    protected final CommandHandlerBuilder builder = new CommandHandlerBuilder();

    public SafeCommandHandlerBuilder(Colonel<S> colonel) {
        this.colonel = colonel;
    }

    //

    public SafeCommandHandlerBuilder<S> path(String path) {
        this.paths.add(path);
        return this;
    }

    //

    protected SafeCommandHandlerBuilder<S> parameter(@NotNull String name,
                                                     @NotNull ReadMode readMode,
                                                     @NotNull CommandParameterParser parser,
                                                     @NotNull CommandParameterCompleter completer) {
        builder.parameter(name, readMode, parser, completer);
        return this;
    }

    //

    public SafeCommandParameterBuilder<S> parameter() {
        return new SafeCommandParameterBuilder<>(this);
    }

    public SafeCommandParameterBuilder<S> string() {
        return parameter().readString();
    }

    public SafeCommandParameterBuilder<S> greedy() {
        return parameter().readGreedy();
    }

    //

    public SafeCommandParameterBuilder<S> parameter(@NotNull String name) {
        return parameter().name(name);
    }

    public SafeCommandParameterBuilder<S> string(@NotNull String name) {
        return parameter(name).readString();
    }

    public SafeCommandParameterBuilder<S> greedy(@NotNull String name) {
        return parameter(name).readGreedy();
    }

    //

    public SafeCommandParameterBuilder<S> parameter(@NotNull String name,
                                                    @NotNull SafeCommandParameterParser<S> parser) {
        return parameter(name).parser(parser);
    }

    public SafeCommandParameterBuilder<S> string(@NotNull String name,
                                                 @NotNull SafeCommandParameterParser<S> parser) {
        return parameter(name, parser).readString();
    }

    public SafeCommandParameterBuilder<S> greedy(@NotNull String name,
                                                 @NotNull SafeCommandParameterParser<S> parser) {
        return parameter(name, parser).readGreedy();
    }

    //

    public SafeCommandParameterBuilder<S> parameter(@NotNull String name,
                                                    @NotNull Function<String, Object> parser) {
        return parameter(name).parser(parser);
    }

    public SafeCommandParameterBuilder<S> string(@NotNull String name,
                                                 @NotNull Function<String, Object> parser) {
        return parameter(name, parser).readString();
    }

    public SafeCommandParameterBuilder<S> greedy(@NotNull String name,
                                                 @NotNull Function<String, Object> parser) {
        return parameter(name, parser).readGreedy();
    }

    //

    public SafeCommandHandlerBuilder<S> literal(@NotNull String literal) {
        return parameter(literal).completer(literal).parser(input -> {
            if ( input.equalsIgnoreCase(literal) ) {
                return literal;
            }
            throw new IllegalArgumentException(String.format("Expected literal '%s', got '%s'", literal, input));
        }).done();
    }

    //

    public SafeCommandHandlerBuilder<S> executor(SafeCommandExecutor<S> executor) {
        builder.executor(context -> executor.execute(new SafeCommandContext<>(context)));
        return this;
    }

    public SafeCommandHandlerBuilder<S> condition(Predicate<S> condition) {
        builder.condition(source -> condition.test((S) source));
        return this;
    }

    //

    public SafeCommandHandlerBuilder<S> source(SafeCommandSourceMapper<S> mapper) {
        builder.source(source -> mapper.map((S) source));
        return this;
    }

    public SafeCommandHandlerBuilder<S> source(@NotNull Class<?> type,
                                               @Nullable String mapperName) {
        SafeCommandSourceMapper<S> mapper;
        if (mapperName != null && !mapperName.isEmpty()) {
            mapper = colonel.registry().mapper(type, mapperName, false)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No mapper with name '%s' found for type %s.", mapperName, type.getName())));
        } else {
            mapper = colonel.registry().mapper(type)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No mapper for type %s found.", type.getName())));
        }
        return source(mapper);
    }

    public SafeCommandHandlerBuilder<S> source(@NotNull Class<?> type) {
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

