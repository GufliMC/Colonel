package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.definition.CommandDefinition;
import com.guflimc.colonel.common.definition.CommandParameter;
import com.guflimc.colonel.common.tree.CommandDelegate;
import com.guflimc.colonel.common.tree.CommandHandler;
import com.guflimc.colonel.common.parser.CommandInput;
import com.guflimc.colonel.common.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

class CommandHandlerImpl<S> extends CommandHandler {

    private final Map<CommandParameter, Function<String, Object>> parameters;

    private final CommandExecutor<S> executor;
    private final CommandCompleter<S> completer;

    public CommandHandlerImpl(@NotNull Map<CommandParameter, Function<String, Object>> parameters,
                              @NotNull CommandExecutor<S> executor,
                              @NotNull CommandCompleter<S> completer) {
        super(new CommandDefinition(parameters.keySet().toArray(CommandParameter[]::new)));
        this.parameters = Map.copyOf(parameters);
        this.executor = executor;
        this.completer = completer;
    }

    @Override
    public CommandDelegate prepare(Object source, CommandInput input) {
        Map<String, Object> arguments = new HashMap<>();
        Map<String, CommandInput.ParseError> errors = new HashMap<>();
        Runnable failure = null;

        for ( CommandParameter param : parameters.keySet() ) {
            if ( input.error(param.name()) != null ) {
                errors.put(param.name(), input.error(param.name()));
                continue;
            }

            String value = (String) input.argument(param.name());
            try {
                Object parsed = parameters.get(param).apply(value);
                if ( parsed instanceof ArgumentParseResult.ArgumentParseSuccess aps ) {
                    arguments.put(param.name(), aps.value);
                } else if ( parsed instanceof ArgumentParseResult.ArgumentParseFail apf ) {
                    errors.put(param.name(), CommandInput.ParseError.INVALID);
                    failure = apf.runnable;
                } else {
                    arguments.put(param.name(), parsed);
                }
                arguments.put(param.name(), parsed);
            } catch (Exception ex) {
                errors.put(param.name(), CommandInput.ParseError.INVALID);
            }
        }

        CommandInput ri = new CommandInput(arguments, errors, new HashMap<>());
        Runnable fi = failure;

        return new CommandDelegate(ri) {
            @Override
            public void run() {
                if ( input.errors().isEmpty() ) {
                    executor.execute(new CommandContext<>((S) source, input));
                    return;
                }

                if ( fi != null ) {
                    fi.run();
                }
            }
        };
    }

    @Override
    public List<Suggestion> suggestions(Object source, CommandInput input) {
        CommandDelegate delegate = prepare(source, input);
        return completer.execute(new CommandContext<>((S) source, delegate.input()));
    }

}
