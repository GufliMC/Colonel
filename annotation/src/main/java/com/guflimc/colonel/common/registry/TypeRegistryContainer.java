package com.guflimc.colonel.common.registry;

import com.guflimc.colonel.common.command.CommandContext;
import com.guflimc.colonel.common.command.CommandSourceContext;
import com.guflimc.colonel.common.command.syntax.CommandParameterParser;
import com.guflimc.colonel.common.command.syntax.CommandParameterSuggestion;
import com.guflimc.colonel.common.command.syntax.CommandParameterSuggestionProvider;
import com.guflimc.colonel.common.command.syntax.CommandParameterType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TypeRegistryContainer {

    // DEFAULTS

    public TypeRegistryContainer() {
        registerParameterType(Integer.class, (ctx, input) -> Integer.parseInt(input));
        registerParameterType(Double.class, (ctx, input) -> Double.parseDouble(input));
        registerParameterType(Float.class, (ctx, input) -> Float.parseFloat(input));
        registerParameterType(Boolean.class, (ctx, input) -> Boolean.parseBoolean(input));
        registerParameterType(Long.class, (ctx, input) -> Long.parseLong(input));
        registerParameterType(Short.class, (ctx, input) -> Short.parseShort(input));
        registerParameterType(Byte.class, (ctx, input) -> Byte.parseByte(input));
        registerParameterType(Character.class, (ctx, input) -> input.charAt(0));
        registerParameterType(String.class, (ctx, input) -> input);
    }

    // PARAMETERS

    private final TypeRegistry<CommandParameterType<?>> parameterTypes = new TypeRegistry<>();

    public <T> void registerParameterType(@NotNull CommandParameterType<T> parameterType) {
        parameterTypes.register(parameterType.type(), parameterType);
    }

    public <T> void registerParameterType(@NotNull Class<T> type,
                                          @NotNull CommandParameterParser<T> parser,
                                          @NotNull CommandParameterSuggestionProvider suggestionProvider) {
        registerParameterType(new CommandParameterType<>(type) {
            @Override
            public List<CommandParameterSuggestion> suggest(@NotNull CommandContext context, @NotNull String input) {
                return suggestionProvider.suggest(context, input);
            }

            @Override
            public T parse(@NotNull CommandContext context, @NotNull String input) {
                return parser.parse(context, input);
            }
        });
    }

    public <T> void registerParameterType(@NotNull Class<T> type,
                                          @NotNull CommandParameterParser<T> parser) {
        registerParameterType(type, parser, (context, input) -> List.of());
    }

    public <T> void registerParameterType(@NotNull String name, @NotNull CommandParameterType<T> parameterType) {
        parameterTypes.register(parameterType.type(), name, parameterType);
    }

    public <T> void registerParameterType(@NotNull Class<T> type, @NotNull String name,
                                          @NotNull CommandParameterParser<T> parser,
                                          @NotNull CommandParameterSuggestionProvider suggestionProvider) {
        registerParameterType(name, new CommandParameterType<>(type) {
            @Override
            public List<CommandParameterSuggestion> suggest(@NotNull CommandContext context, @NotNull String input) {
                return suggestionProvider.suggest(context, input);
            }

            @Override
            public T parse(@NotNull CommandContext context, @NotNull String input) {
                return parser.parse(context, input);
            }
        });
    }

    public <T> void registerParameterType(@NotNull Class<T> type, @NotNull String name,
                                          @NotNull CommandParameterParser<T> parser) {
        registerParameterType(type, name, parser, (context, input) -> List.of());
    }

    //

    public <T> void unregisterParameterType(@NotNull Class<?> type, @NotNull String name) {
        parameterTypes.unregister(type, name);
    }

    //

    @SuppressWarnings("unchecked")
    public <T> CommandParameterType<T> parameterType(@NotNull Class<T> type, @NotNull String name) {
        return parameterTypes.find(type, name)
                .map(r -> (CommandParameterType<T>) r)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No parameter parser with name '%s' and type %s found.", name, type.getSimpleName())));
    }

    @SuppressWarnings("unchecked")
    public <T> CommandParameterType<T> parameterType(@NotNull Class<T> type) {
        return parameterTypes.find(type)
                .map(r -> (CommandParameterType<T>) r)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No parameter parser with type %s found.", type.getSimpleName())));
    }

    // COMMAND SOURCES

    @FunctionalInterface
    public interface SourceType<T> {
        T value(CommandSourceContext context);
    }

    private final TypeRegistry<SourceType<?>> sourceTypes = new TypeRegistry<>();

    //

    public <T> void registerSourceType(@NotNull Class<T> type, @NotNull TypeRegistryContainer.SourceType<T> parser) {
        sourceTypes.register(type, parser);
    }

    public <T> void registerSourceType(@NotNull Class<T> type, @NotNull String name, @NotNull TypeRegistryContainer.SourceType<T> parser) {
        sourceTypes.register(type, name, parser);
    }

    //

    public <T> void unregisterSourceType(@NotNull Class<T> type, @NotNull String name) {
        sourceTypes.unregister(type, name);
    }

    public <T> void unregisterSourceType(@NotNull Class<T> type) {
        sourceTypes.unregister(type);
    }

    //

    @SuppressWarnings("unchecked")
    public <T> SourceType<T> sourceType(@NotNull Class<T> type, @NotNull String name) {
        return sourceTypes.find(type, name)
                .map(r -> (SourceType<T>) r)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No source parser with name '%s' and type %s found.", name, type.getSimpleName())));
    }

    @SuppressWarnings("unchecked")
    public <T> SourceType<T> sourceType(@NotNull Class<T> type) {
        return sourceTypes.find(type)
                .map(r -> (SourceType<T>) r)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No source parser with type %s found.", type.getSimpleName())));
    }
}
