package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.definition.CommandDefinition;
import com.guflimc.colonel.common.definition.CommandParameter;
import com.guflimc.colonel.common.parser.CommandInput;
import com.guflimc.colonel.common.parser.CommandInputArgument;
import com.guflimc.colonel.common.parser.CommandInputBuilder;
import com.guflimc.colonel.common.suggestion.Suggestion;
import com.guflimc.colonel.common.tree.CommandDelegate;
import com.guflimc.colonel.common.tree.CommandHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class CommandHandlerImpl<S> extends CommandHandler {

    private final List<CommandParameterWrapper<S>> parameters;
    private final CommandExecutor<S> executor;

    public CommandHandlerImpl(@NotNull List<CommandParameterWrapper<S>> parameters,
                              @NotNull CommandExecutor<S> executor) {
        super(new CommandDefinition(parameters.stream().map(CommandParameterWrapper::parameter).toArray(CommandParameter[]::new)));
        this.parameters = List.copyOf(parameters);
        this.executor = executor;
    }

    private CommandContext<S> context(Object source, CommandInputBuilder b) {
        return new CommandContext<>(definition(), (S) source, b.build());
    }

    @Override
    public CommandDelegateImpl<S> prepare(Object source, CommandInput input) {
        CommandInputBuilder builder = CommandInputBuilder.builder();
        Runnable failure = null;

        for ( CommandParameterWrapper<S> param : parameters ) {
            // missing error
            if ( input.failure(param.parameter()) ) {
                builder.fail(param.parameter(), input.error(param.parameter()));
                continue;
            }

            // parse value
            String value = (String) input.argument(param.parameter());
            try {
                CommandContext<S> ctx = context(source, builder);
                Argument arg = param.parser().parse(ctx, value);

                if ( arg instanceof Argument.ArgumentSuccess as) {
                    builder.success(param.parameter(), as.value);
                } else if ( arg instanceof Argument.ArgumentFailure af ) {
                    builder.fail(param.parameter(), CommandInputArgument.ArgumentFailureType.INVALID);
                    failure = af.runnable;
                }
            } catch (Exception ex) {
                builder.fail(param.parameter(), CommandInputArgument.ArgumentFailureType.INVALID);
            }
        }

        builder.withCursor(input.cursor());

        CommandContext<S> ctx = context(source, builder);
        return new CommandDelegateImpl<>(ctx, executor, failure);
    }

    @Override
    public List<Suggestion> suggestions(Object source, CommandInput input) {
        CommandDelegateImpl<S> delegate = prepare(source, input);
        if ( delegate.input().cursor() == null ) {
            return List.of();
        }

        CommandParameterWrapper<S> param = parameters.stream()
                .filter(p -> p.parameter().equals(delegate.input().cursor()))
                .findFirst().orElseThrow();

        String str = (String) input.argument(param.parameter());
        return param.completer().suggestions(delegate.context, str);
    }

}
