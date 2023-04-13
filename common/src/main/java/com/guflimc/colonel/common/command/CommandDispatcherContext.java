package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.command.handler.CommandParameterParser;
import com.guflimc.colonel.common.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class CommandDispatcherContext {

    // PARAMETERS

    public record ParameterParser<T>(CommandParameterParser<T> parser, Collection<String> dependencies) {
    }

    private final Registry<ParameterParser<?>> parameterParsers = new Registry<>();

    public <T> void registerParameterParser(@NotNull String name, @NotNull Class<T> type, @NotNull CommandParameterParser<T> parser) {
        registerParameterParser(name, type, parser, Set.of());
    }

    public <T> void registerParameterParser(@NotNull String name, @NotNull Class<T> type, @NotNull CommandParameterParser<T> parser, @NotNull Collection<String> dependencies) {
        parameterParsers.register(name, type, new ParameterParser<>(parser, dependencies));
    }

    public <T> void unregisterParameterParser(@NotNull String name) {
        parameterParsers.unregister(name);
    }

    public <T> ParameterParser<T> parameterParser(@NotNull Class<T> type, @NotNull String name) {
        return parameterParsers.find(name, type)
                .map(r -> (ParameterParser<T>) r)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No parameter parser with name '%s' and type %s found.", name, type.getSimpleName())));
    }

    public <T> ParameterParser<T> parameterParser(@NotNull Class<T> type) {
        return parameterParsers.find(type)
                .map(r -> (ParameterParser<T>) r)
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
