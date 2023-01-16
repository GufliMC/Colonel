package com.guflimc.colonel.common;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class ColonelConfig<S> {

    public ColonelConfig() {
        withArgumentType(Integer.class, parameter -> IntegerArgumentType.integer());
        withArgumentType(int.class, parameter -> IntegerArgumentType.integer());
        withArgumentType(Long.class, parameter -> LongArgumentType.longArg());
        withArgumentType(long.class, parameter -> LongArgumentType.longArg());
        withArgumentType(Double.class, parameter -> DoubleArgumentType.doubleArg());
        withArgumentType(double.class, parameter -> DoubleArgumentType.doubleArg());
        withArgumentType(Float.class, parameter -> FloatArgumentType.floatArg());
        withArgumentType(float.class, parameter -> FloatArgumentType.floatArg());
        withArgumentType(Boolean.class, parameter -> BoolArgumentType.bool());
        withArgumentType(boolean.class, parameter -> BoolArgumentType.bool());
        withArgumentType(String.class, parameter -> StringArgumentType.string());
    }
    
    // PERMISSION VALIDATOR

    // null = ignore permission annotations (default)
    private BiPredicate<S, String> permissionTester = null;

    public ColonelConfig<S> withPermmissionTester(BiPredicate<S, String> permissionTester) {
        this.permissionTester = permissionTester;
        return this;
    }

    public BiPredicate<S, String> permissionTester() {
        return permissionTester;
    }

    // ARGUMENT MAPPERS

    private final Map<Class<?>, ArgumentMapper<?>> argumentTypes = new ConcurrentHashMap<>();

    @FunctionalInterface
    public interface ArgumentMapper<T> {
        ArgumentType<T> map(Parameter parameter);
    }

    public <T> void withArgumentType(Class<T> type, ArgumentMapper<T> mapper) {
        argumentTypes.put(type, mapper);
    }

    public <T> void withArgumentTypeParser(Class<T> type, Function<String, T> parser) {
        argumentTypes.put(type, (ArgumentMapper<T>) ignored -> (ArgumentType<T>) reader -> parser.apply(reader.readString()));
    }

    public Optional<ArgumentType<?>> argumentType(Parameter parameter) {
        return this.<ArgumentMapper<?>>find(argumentTypes, parameter.getType()).map(mapper -> mapper.map(parameter));
    }

    // SUGGESTION SUPPLIERS

    private final List<SuggestionSupplier<S, ?>> suggestionSuppliers = new ArrayList<>();

    public record SuggestionSupplier<S, T>(@Nullable String name,
                                           @NotNull Class<T> type,
                                           @NotNull SuggestionProvider<S> provider) {
    }

    public <T> void withSuggestionSupplier(@Nullable String name, @NotNull Class<T> type, @NotNull SuggestionProvider<S> provider) {
        suggestionSuppliers.add(new SuggestionSupplier<>(name, type, provider));
    }

    public <T> void withSuggestionSupplier(@NotNull Class<T> type, @NotNull com.mojang.brigadier.suggestion.SuggestionProvider<S> provider) {
        withSuggestionSupplier(null, type, provider);
    }

    public Optional<SuggestionProvider<S>> suggestionSupplier(@Nullable String name, @NotNull Class<?> type) {
        return suggestionSuppliers.stream().filter(p -> p.type.equals(type))
                .filter(p -> name == null || name.equals(p.name)) // if name is given, it must match
                .findFirst().map(rsp -> rsp.provider);
    }

    // COMMAND SOURCE MAPPERS

    private final Map<Class<?>, CommandSourceMapper<S, ?>> commandSources = new ConcurrentHashMap<>();

    @FunctionalInterface
    public interface CommandSourceMapper<S, T> {
        T map(CommandContext<S> context);
    }

    public <T> void withCommandSource(Class<T> type, CommandSourceMapper<S, T> mapper) {
        commandSources.put(type, mapper);
    }

    public Optional<Object> commandSource(CommandContext<S> context, Class<?> type) {
        return find(commandSources, type).map(mapper -> mapper.map(context));
    }

    //

    private <M> Optional<M> find(Map<Class<?>, M> map, Class<?> type) {
        for (Class<?> cls : map.keySet()) {
            if (cls.equals(type)) {
                return Optional.ofNullable((M) map.get(type));
            }
        }
        for (Class<?> cls : argumentTypes.keySet()) {
            if (cls.isAssignableFrom(type)) {
                return Optional.ofNullable((M) map.get(type));
            }
        }
        return Optional.empty();
    }
}
