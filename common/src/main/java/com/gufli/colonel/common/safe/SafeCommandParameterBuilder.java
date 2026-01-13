package com.gufli.colonel.common.safe;

import com.gufli.colonel.common.build.CommandParameterCompleter;
import com.gufli.colonel.common.build.CommandParameterParser;
import com.gufli.colonel.common.dispatch.definition.ReadMode;
import com.gufli.colonel.common.dispatch.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SafeCommandParameterBuilder<S> {

    protected final SafeCommandHandlerBuilder<S> builder;

    protected String name;
    protected ReadMode readMode = ReadMode.STRING;
    protected CommandParameterParser parser;
    protected CommandParameterCompleter completer = (context, input) -> List.of();

    SafeCommandParameterBuilder(SafeCommandHandlerBuilder<S> builder) {
        this.builder = builder;
    }

    //

    public SafeCommandParameterBuilder<S> name(@NotNull String name) {
        this.name = name;
        return this;
    }

    //

    public SafeCommandParameterBuilder<S> readMode(@NotNull ReadMode readMode) {
        this.readMode = readMode;
        return this;
    }

    public SafeCommandParameterBuilder<S> readString() {
        return readMode(ReadMode.STRING);
    }

    public SafeCommandParameterBuilder<S> readGreedy() {
        return readMode(ReadMode.GREEDY);
    }

    //

    public SafeCommandParameterBuilder<S> parser(@NotNull SafeCommandParameterParser<S> parser) {
        this.parser = (context, input) -> parser.parse(new SafeCommandContext<>(context), input);
        return this;
    }

    public SafeCommandParameterBuilder<S> parser(@NotNull Function<String, Object> parser) {
        return parser((context, input) -> parser.apply(input));
    }

    public <T> SafeCommandParameterBuilder<S> parser(@NotNull Class<T> type, @Nullable String name) {
        SafeCommandParameterParser<S> parser;
        if (name != null && !name.isEmpty()) {
            parser = builder.colonel.registry().parser(type, name, false)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No parser with name '%s' found for type %s.", name, type.getName())));
        } else if (type.isEnum()) {
            parser = builder.colonel.registry().parser(type, this.name, true)
                    .orElse(null);

            // ENUM DEFAULT PARSER
            if (parser == null) {
                parser = (context, input) ->
                        Arrays.stream(type.getEnumConstants())
                                .filter(con -> ((Enum<?>) con).name().equalsIgnoreCase(input))
                                .findFirst().map(con -> (Object) con)
                                .orElseThrow(() -> new IllegalArgumentException("No enum constant " + input + " found for type " + type.getName() + "."));
            }
        } else {
            parser = builder.colonel.registry().parser(type, this.name, true)
                    .orElseThrow(() -> new IllegalArgumentException("No parser for type " + type.getName() + " found."));
        }

        return parser(parser);
    }

    public <T> SafeCommandParameterBuilder<S> parser(@NotNull Class<T> type) {
        return parser(type, null);
    }

    //

    public SafeCommandParameterBuilder<S> completer(SafeCommandParameterCompleter<S> completer) {
        this.completer = (context, input) -> completer.suggestions(new SafeCommandContext<>(context), input);
        return this;
    }

    public SafeCommandParameterBuilder<S> completerWithMatchCheck(@NotNull SafeCommandParameterCompleter<S> completer) {
        return completer(SafeCommandParameterCompleter.withMatchCheck(completer));
    }

    public SafeCommandParameterBuilder<S> completer(@NotNull Function<String, List<Suggestion>> completer) {
        return completer((context, input) -> completer.apply(input));
    }

    public SafeCommandParameterBuilder<S> completerWithMatchCheck(@NotNull Function<String, List<Suggestion>> completer) {
        return completer(SafeCommandParameterCompleter.withMatchCheck((context, input) -> completer.apply(input)));
    }

    public SafeCommandParameterBuilder<S> completer(List<Suggestion> suggestions) {
        return completer(SafeCommandParameterCompleter.withMatchCheck((context, input) -> suggestions));
    }

    public SafeCommandParameterBuilder<S> completer(Suggestion... suggestions) {
        return completer(List.of(suggestions));
    }

    public SafeCommandParameterBuilder<S> completer(String... suggestions) {
        List<Suggestion> result = Arrays.stream(suggestions).map(Suggestion::new).toList();
        return completer(result);
    }

    public <T> SafeCommandParameterBuilder<S> completer(@NotNull Class<T> type, @Nullable String name) {
        SafeCommandParameterCompleter<S> completer;
        if (name != null && !name.isEmpty()) {
            completer = builder.colonel.registry().completer(type, name, false)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No completer with name '%s' found for type %s.", name, type.getName())));
        } else if (type.isEnum()) {
            completer = builder.colonel.registry().completer(type, this.name, true).orElse(null);

            if (completer == null) {
                completer = (context, input) -> {
                    String linput = input.toLowerCase();
                    return Arrays.stream(type.getEnumConstants())
                            .map(con -> (Enum<?>) con)
                            .map(Enum::name)
                            .filter(n -> n.toLowerCase().startsWith(linput))
                            .map(Suggestion::new)
                            .toList();
                };
            }
        } else {
            completer = builder.colonel.registry().completer(type, this.name, true)
                    .orElseGet(() -> (ctx, input) -> List.of());
        }

        return completer(completer);
    }

    public <T> SafeCommandParameterBuilder<S> completer(@NotNull Class<T> type) {
        return completer(type, null);
    }

    //

    public <T> SafeCommandParameterBuilder<S> type(@NotNull Class<T> type) {
        return parser(type).completer(type);
    }

    public <T> SafeCommandParameterBuilder<S> type(@NotNull Class<T> type, @NotNull String parserName, @NotNull String mapperName) {
        return parser(type, parserName).completer(type, mapperName);
    }

    public <T> SafeCommandParameterBuilder<S> type(@NotNull Class<T> type, @NotNull String parserMapperName) {
        return type(type, parserMapperName, parserMapperName);
    }

    //

    public SafeCommandHandlerBuilder<S> done() {
        if (name == null) {
            throw new IllegalStateException("Name is not set.");
        }
        if (parser == null) {
            throw new IllegalStateException("Parser is not set.");
        }

        builder.parameter(name, readMode, parser, completer);
        return builder;
    }

}
