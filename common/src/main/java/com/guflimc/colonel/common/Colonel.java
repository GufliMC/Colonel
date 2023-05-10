package com.guflimc.colonel.common;

import com.guflimc.colonel.common.build.HandleFailure;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.dispatch.tree.CommandHandler;
import com.guflimc.colonel.common.dispatch.tree.CommandTree;
import com.guflimc.colonel.common.exception.CommandNotFoundException;
import com.guflimc.colonel.common.safe.FunctionRegistry;
import com.guflimc.colonel.common.safe.SafeCommandHandlerBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    public SafeCommandHandlerBuilder<S> builder() {
        return new SafeCommandHandlerBuilder<>(this);
    }

    //

    public void dispatch(S source, String input) {
        if (tree.apply(source, input)) {
            return;
        }

        throw new CommandNotFoundException("Command not found: " + input);
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
        registry.registerParameterParser(Boolean.class, (ctx, value) -> {
            if (value.equalsIgnoreCase("true")) {
                return true;
            }
            if (value.equalsIgnoreCase("false")) {
                return false;
            }
            throw HandleFailure.of("Invalid boolean value: " + value);
        });
        registry.registerParameterCompleter(Boolean.class, () -> List.of("true", "false"));
    }

}
