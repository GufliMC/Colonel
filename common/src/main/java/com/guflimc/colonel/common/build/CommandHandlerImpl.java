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
import java.util.function.Function;

class CommandHandlerImpl<S> extends CommandHandler {

    private final Map<CommandParameter, Function<String, Object>> parameters;
    private final CommandExecutor<S> executor;

    public CommandHandlerImpl(@NotNull Map<CommandParameter, Function<String, Object>> parameters, @NotNull CommandExecutor<S> executor) {
        super(new CommandDefinition(parameters.keySet().toArray(CommandParameter[]::new)));
        this.parameters = Map.copyOf(parameters);
        this.executor = executor;
    }

    @Override
    public CommandDelegate prepare(Object source, CommandInput input) {
        input = parse(input);
        return new CommandDelegate(input) {
            @Override
            public void run() {
                executor.execute(new CommandContext<>((S) source, this.input));
            }
        };
    }

    @Override
    public List<Suggestion> suggestions(Object source, CommandInput input) {
        input = parse(input);
        // TODO suggestion provider
        return super.suggestions(source, input);
    }

    //

    private CommandInput parse(@NotNull CommandInput input) {
        Map<String, Object> arguments = new HashMap<>();
        Map<String, CommandInput.ParseError> errors = new HashMap<>();

        for ( CommandParameter param : parameters.keySet() ) {
            if ( input.error(param.name()) != null ) {
                errors.put(param.name(), input.error(param.name()));
                continue;
            }

            String value = (String) input.argument(param.name());
            try {
                Object parsed = parameters.get(param).apply(value);
                arguments.put(param.name(), parsed);
            } catch (Exception ex) {
                // TODO message in delegate
                errors.put(param.name(), CommandInput.ParseError.INVALID);
            }
        }

        return new CommandInput(arguments, errors, new HashMap<>());
    }

}
