package com.guflimc.colonel.common.registry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ArgumentMapperRegistry {

    private final Map<Class<?>, Mapper<?>> mappers = new ConcurrentHashMap<>();

    @FunctionalInterface
    public interface Mapper<T> {
        ArgumentType<T> map(Parameter parameter);
    }

    private record CustomArgumentType<T>(Function<String, T> parser) implements ArgumentType<T> {
        @Override
        public T parse(StringReader reader) throws CommandSyntaxException {
            return parser.apply(reader.readString());
        }
    }

    //

    public <T> void register(Class<T> type, Mapper<T> mapper) {
        mappers.put(type, mapper);
    }

    public <T> void registerParser(Class<T> type, Function<String, T> parser) {
        mappers.put(type, (Mapper<T>) parameter -> new CustomArgumentType<>(parser));
    }

    public Optional<ArgumentType<?>> map(Parameter parameter) {
        return find(parameter.getType()).map(mapper -> mapper.map(parameter));
    }

    //

    private Optional<Mapper<?>> find(Class<?> type) {
        for (Class<?> cls : mappers.keySet()) {
            if (cls.equals(type)) {
                return Optional.ofNullable(mappers.get(type));
            }
        }

        for (Class<?> cls : mappers.keySet()) {
            if (cls.isAssignableFrom(type)) {
                return Optional.ofNullable(mappers.get(type));
            }
        }

        return Optional.empty();
    }

    //

    public ArgumentMapperRegistry() {
        register(Integer.class, parameter -> IntegerArgumentType.integer());
        register(int.class, parameter -> IntegerArgumentType.integer());
        register(Long.class, parameter -> LongArgumentType.longArg());
        register(long.class, parameter -> LongArgumentType.longArg());
        register(Double.class, parameter -> DoubleArgumentType.doubleArg());
        register(double.class, parameter -> DoubleArgumentType.doubleArg());
        register(Float.class, parameter -> FloatArgumentType.floatArg());
        register(float.class, parameter -> FloatArgumentType.floatArg());
        register(Boolean.class, parameter -> BoolArgumentType.bool());
        register(boolean.class, parameter -> BoolArgumentType.bool());
        register(String.class, parameter -> StringArgumentType.string());
    }

    //

}
