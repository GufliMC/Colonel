package com.guflimc.colonel.common;

import com.guflimc.colonel.common.build.CommandHandlerBuilder;
import com.guflimc.colonel.common.build.CommandParameterCompleter;
import com.guflimc.colonel.common.build.CommandParameterParser;
import com.guflimc.colonel.common.build.FunctionRegistry;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.dispatch.tree.CommandHandler;
import com.guflimc.colonel.common.dispatch.tree.CommandTree;
import com.guflimc.colonel.common.ext.Argument;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Colonel<S> {

    private final FunctionRegistry<S> registry = new FunctionRegistry<>();
    private final CommandTree tree = new CommandTree();

    public Colonel() {
        registerDefaultTypes();
    }

    public FunctionRegistry<S> registry() {
        return registry;
    }

    //

    public void register(@NotNull String path, @NotNull CommandHandler handler) {
        tree.register(path, handler);
    }

    public CommandHandlerBuilder<S> builder() {
        return new CommandHandlerBuilder<>(this);
    }

    //

    public void dispatch(S source, String input) {
        if (tree.apply(source, input)) {
            return;
        }

        // TODO handler not found
        System.out.println("not found");
    }

    //

    public List<Suggestion> suggestions(S source, String input) {
        return tree.suggestions(source, input, input.length());
    }

    public List<Suggestion> suggestions(S source, String input, int cursor) {
        return tree.suggestions(source, input, cursor);
    }

    // INTERNAL

    private void registerDefaultTypes() {
        registry.registerParameterParser(String.class, wrap(s -> s));
        registry.registerParameterParser(Integer.class, wrap(Integer::parseInt));
        registry.registerParameterParser(Long.class, wrap(Long::parseLong));
        registry.registerParameterParser(Float.class, wrap(Float::parseFloat));
        registry.registerParameterParser(Double.class, wrap(Double::parseDouble));
        registry.registerParameterParser(Byte.class, wrap(Byte::parseByte));
        registry.registerParameterParser(Short.class, wrap(Short::parseShort));
        registry.registerParameterParser(LocalTime.class, wrap(LocalTime::parse));
        registry.registerParameterParser(LocalDate.class, wrap(LocalDate::parse));
        registry.registerParameterParser(LocalDateTime.class, wrap(LocalDateTime::parse));
        registry.registerParameterParser(Boolean.class, (ctx, value) -> {
            if (value.equalsIgnoreCase("true") || value.equals("1")
                    || value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")) {
                return Argument.success(true);
            }
            if (value.equalsIgnoreCase("false") || value.equals("0")
                    || value.equalsIgnoreCase("n") || value.equalsIgnoreCase("no")) {
                return Argument.success(false);
            }
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Invalid boolean value: " + value);
            });
        });
        registry.registerParameterCompleter(Boolean.class, CommandParameterCompleter.withMatchCheck((ctx, input) -> Stream.of("true", "false")
                .filter(s -> s.startsWith(input.toLowerCase()))
                .map(Suggestion::new).toList()));
    }

    private CommandParameterParser<S> wrap(@NotNull Function<String, Object> parser) {
        return (ctx, input) -> {
            try {
                return Argument.success(parser.apply(input));
            } catch (Throwable e) {
                return Argument.fail(() -> {
                    throw e;
                });
            }
        };
    }

}
