package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.dispatch.definition.ReadMode;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandParameterCompleter;
import com.guflimc.colonel.common.ext.ExtCommandParameterParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CommandParameterBuilder<S> {

    protected final CommandHandlerBuilder<S> builder;

    protected String name;
    protected ReadMode readMode = ReadMode.STRING;
    protected ExtCommandParameterParser parser;
    protected ExtCommandParameterCompleter completer = (context, input) -> List.of();

    CommandParameterBuilder(CommandHandlerBuilder<S> builder) {
        this.builder = builder;
    }

    //

    public CommandParameterBuilder<S> name(@NotNull String name) {
        this.name = name;
        return this;
    }

    //

    public CommandParameterBuilder<S> readMode(@NotNull ReadMode readMode) {
        this.readMode = readMode;
        return this;
    }

    public CommandParameterBuilder<S> readString() {
        return readMode(ReadMode.STRING);
    }

    public CommandParameterBuilder<S> readGreedy() {
        return readMode(ReadMode.GREEDY);
    }

    //

    public CommandParameterBuilder<S> parser(@NotNull CommandParameterParser<S> parser) {
        this.parser = (context, input) -> parser.parse(new CommandContext<>(context), input);
        return this;
    }

    public CommandParameterBuilder<S> parser(@NotNull BiFunction<CommandContext<S>, String, Object> parser) {
        return parser((CommandParameterParser<S>) (context, input) -> {
            try {
                return Argument.success(parser.apply(context, input));
            } catch (Throwable e) {
                return Argument.fail(() -> {
                    throw e;
                });
            }
        });
    }

    public CommandParameterBuilder<S> parser(@NotNull Function<String, Object> parser) {
        return parser((BiFunction<CommandContext<S>, String, Object>) (context, input) -> parser.apply(input));
    }

    public <T> CommandParameterBuilder<S> parser(@NotNull Class<T> type, @Nullable String name) {
        CommandParameterParser<S> parser;
        if (name != null && !name.isEmpty()) {
            parser = builder.colonel.registry().parser(type, name, false)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No parser with name '%s' found for type %s.", name, type.getName())));
        } else {
            parser = builder.colonel.registry().parser(type, this.name, true)
                    .orElseThrow(() -> new IllegalArgumentException("No parser for type " + type.getName() + " found."));
        }
        
        return parser(parser);
    }

    public <T> CommandParameterBuilder<S> parser(@NotNull Class<T> type) {
        return parser(type, null);
    }
    
    //

    public CommandParameterBuilder<S> completer(CommandParameterCompleter<S> completer) {
        this.completer = (context, input) -> completer.suggestions(new CommandContext<>(context), input);
        return this;
    }

    public CommandParameterBuilder<S> completerWithMatchCheck(@NotNull CommandParameterCompleter<S> completer) {
        return completer(CommandParameterCompleter.withMatchCheck(completer));
    }

    public CommandParameterBuilder<S> completer(@NotNull Function<String, List<Suggestion>> completer) {
        return completer((context, input) -> completer.apply(input));
    }

    public CommandParameterBuilder<S> completerWithMatchCheck(@NotNull Function<String, List<Suggestion>> completer) {
        return completer(CommandParameterCompleter.withMatchCheck((context, input) -> completer.apply(input)));
    }

    public CommandParameterBuilder<S> completer(List<Suggestion> suggestions) {
        return completer(CommandParameterCompleter.withMatchCheck((context, input) -> suggestions));
    }

    public CommandParameterBuilder<S> completer(Suggestion... suggestions) {
        return completer(List.of(suggestions));
    }

    public CommandParameterBuilder<S> completer(String... suggestions) {
        List<Suggestion> result = Arrays.stream(suggestions).map(Suggestion::new).toList();
        return completer(result);
    }

    public <T> CommandParameterBuilder<S> completer(@NotNull Class<T> type, @Nullable String name) {
        CommandParameterCompleter<S> completer;
        if (name != null && !name.isEmpty()) {
            completer = builder.colonel.registry().completer(type, name, false)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No completer with name '%s' found for type %s.", name, type.getName())));
        } else {
            completer = builder.colonel.registry().completer(type, this.name, true)
                    .orElseGet(() -> (ctx, input) -> List.of());
        }

        return completer(completer);
    }

    public <T> CommandParameterBuilder<S> completer(@NotNull Class<T> type) {
        return completer(type, null);
    }

    //

    public CommandHandlerBuilder<S> done() {
        if ( name == null ) {
            throw new IllegalStateException("Name is not set.");
        }
        if ( parser == null ) {
            throw new IllegalStateException("Parser is not set.");
        }

        builder.parameter(name, readMode, parser, completer);
        return builder;
    }

}
