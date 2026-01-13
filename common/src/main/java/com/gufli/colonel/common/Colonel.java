package com.gufli.colonel.common;

import com.gufli.colonel.common.build.FailureHandler;
import com.gufli.colonel.common.dispatch.suggestion.Suggestion;
import com.gufli.colonel.common.dispatch.tree.CommandHandler;
import com.gufli.colonel.common.dispatch.tree.CommandTree;
import com.gufli.colonel.common.exception.CommandFailure;
import com.gufli.colonel.common.exception.CommandNotFoundFailure;
import com.gufli.colonel.common.safe.FunctionRegistry;
import com.gufli.colonel.common.safe.SafeCommandHandlerBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Colonel<S> {

    private final Map<String, String> placeholders = new HashMap<>();
    private final FunctionRegistry<S> registry = new FunctionRegistry<>();
    private final CommandTree tree = new CommandTree();

    public Colonel() {
        registerDefaultTypes();
    }

    public FunctionRegistry<S> registry() {
        return registry;
    }

    //

    public void placeholder(String placeholder, String value) {
        placeholders.put(placeholder, value);
    }

    protected String replacePlaceholders(String value) {
        for (String placeholder : placeholders.keySet()) {
            value = value.replace("%" + placeholder + "%", placeholders.get(placeholder));
        }
        return value;
    }

    //

    public void register(@NotNull String path, @NotNull CommandHandler handler) {
        path = replacePlaceholders(path);

        tree.register(path, handler);
    }

    public SafeCommandHandlerBuilder<S> builder() {
        return new SafeCommandHandlerBuilder<>(this);
    }

    //

    public void dispatch(S source, String input) {
        try {
            if (tree.apply(source, input)) {
                return;
            }
        } catch (CommandFailure f) {
            if ( f.getCause() instanceof FailureHandler fh ) {
                fh.handler().run();
                return;
            }

            throw f.withCommand(input);
        }

        throw new CommandNotFoundFailure()
                .withCommand(input);
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
        registry.registerParameterParser(String.class, s -> s);
        registry.registerParameterParser(Integer.class, s -> Integer.parseInt(s));
        registry.registerParameterParser(Long.class, s -> Long.parseLong(s));
        registry.registerParameterParser(Float.class, Float::parseFloat);
        registry.registerParameterParser(Double.class, Double::parseDouble);
        registry.registerParameterParser(Byte.class, s -> Byte.parseByte(s));
        registry.registerParameterParser(Short.class, s -> Short.parseShort(s));
        registry.registerParameterParser(LocalTime.class, s -> LocalTime.parse(s));
        registry.registerParameterParser(LocalDate.class, s -> LocalDate.parse(s));
        registry.registerParameterParser(LocalDateTime.class, s -> LocalDateTime.parse(s));
        registry.registerParameterParser(Boolean.class, Boolean::parseBoolean);
        registry.registerParameterCompleter(Boolean.class, () -> List.of("true", "false"));
    }

}
