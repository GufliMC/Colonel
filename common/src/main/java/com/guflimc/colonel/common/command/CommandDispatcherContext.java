package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.command.syntax.CommandParameterParser;
import com.guflimc.colonel.common.command.syntax.CommandParameterSuggestion;
import com.guflimc.colonel.common.command.syntax.CommandParameterSuggestionProvider;
import com.guflimc.colonel.common.command.syntax.CommandParameterType;
import com.guflimc.colonel.common.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandDispatcherContext {

    // PARAMETERS

    private final Registry<CommandParameterType<?>> parameterTypes = new Registry<>();

    public <T> void registerParameterType(@NotNull String name, @NotNull Class<T> type,
                                          @NotNull CommandParameterParser<T> parser,
                                          @NotNull CommandParameterSuggestionProvider suggestionProvider) {
        CommandParameterType<T> parameterType = new CommandParameterType<>(name, type) {
            @Override
            public List<CommandParameterSuggestion> suggest(@NotNull CommandContext context, @NotNull String input) {
                return suggestionProvider.suggest(context, input);
            }

            @Override
            public T parse(@NotNull CommandContext context, @NotNull String input) {
                return parser.parse(context, input);
            }
        };
        parameterTypes.register(name, type, parameterType);
    }

    public <T> void registerParameterType(@NotNull String name, @NotNull Class<T> type,
                                          @NotNull CommandParameterParser<T> parser) {
        registerParameterType(name, type, parser, (context, input) -> List.of());
    }

    public <T> void registerParameterType(@NotNull CommandParameterType<T> parameterType) {
        parameterTypes.register(parameterType.name(), parameterType.type(), parameterType);
    }

    public <T> void unregisterParameterType(@NotNull String name) {
        parameterTypes.unregister(name);
    }

    public <T> CommandParameterType<T> parameterType(@NotNull Class<T> type, @NotNull String name) {
        return parameterTypes.find(name, type)
                .map(r -> (CommandParameterType<T>) r)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No parameter parser with name '%s' and type %s found.", name, type.getSimpleName())));
    }

    public <T> CommandParameterType<T> parameterType(@NotNull Class<T> type) {
        return parameterTypes.find(type)
                .map(r -> (CommandParameterType<T>) r)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No parameter parser with type %s found.", type.getSimpleName())));
    }

    // COMMAND SOURCES

    @FunctionalInterface
    public interface SourceParser<T> {
        T parse(CommandSourceContext context);
    }

    private final Registry<SourceParser<?>> sourceParsers = new Registry<>();

    public <T> void registerSourceParser(@NotNull String name, @NotNull Class<T> type, @NotNull SourceParser<T> parser) {
        sourceParsers.register(name, type, parser);
    }

    public <T> void unregisterSourceParser(@NotNull String name) {
        sourceParsers.unregister(name);
    }

    public <T> SourceParser<T> sourceParser(@NotNull String name) {
        return sourceParsers.find(name)
                .map(r -> (SourceParser<T>) r)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No source parser with name '%s' found.", name)));
    }

    public <T> SourceParser<T> sourceParser(@NotNull Class<T> type) {
        return sourceParsers.find(type)
                .map(r -> (SourceParser<T>) r)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No source parser with type %s found.", type.getSimpleName())));
    }
}
