package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.dispatch.definition.CommandDefinition;
import com.guflimc.colonel.common.dispatch.parser.CommandInput;
import com.guflimc.colonel.common.dispatch.parser.CommandInputArgument;
import com.guflimc.colonel.common.dispatch.parser.CommandInputBuilder;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.exception.CommandMiddlewareException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class CommandHandler extends com.guflimc.colonel.common.dispatch.tree.CommandHandler {

    private final CommandParameter[] parameters;
    private final CommandExecutor executor;
    private final Predicate<Object> condition;
    private final CommandSourceMapper[] mappers;

    CommandHandler(@NotNull CommandParameter[] parameters,
                   @NotNull CommandExecutor executor,
                   @NotNull CommandSourceMapper[] mappers,
                   @NotNull Predicate<Object> condition) {
        super(new CommandDefinition(parameters));
        this.parameters = parameters;
        this.executor = executor;
        this.mappers = mappers;
        this.condition = condition;
    }

    private CommandContext context(Object source, Object[] sources, CommandInputBuilder b) {
        return new CommandContext(b.build(), source, sources);
    }

    @Override
    public CommandDelegate prepare(Object source, CommandInput input) {
        CommandInputBuilder builder = CommandInputBuilder.builder();
        Runnable failure = null;

        Object[] sources = new Object[0];
        try {
            sources = Arrays.stream(mappers)
                    .map(m -> m.map(source))
                    .toArray(Object[]::new);
        } catch (CommandMiddlewareException ex) {
            failure = ex.handler();
        } catch (Exception ex) {
            failure = () -> { throw new RuntimeException("Error occured during source mapping.", ex); };
        }

        for ( CommandParameter param : parameters ) {
            // missing error
            if ( input.failure(param) ) {
                builder.fail(param, input.error(param));
                if ( failure == null ) {
                    failure = () -> { throw new IllegalArgumentException("Missing argument: " + param.name()); };
                }
                continue;
            }

            // parse value
            String value = (String) input.argument(param);
            try {
                CommandContext ctx = context(source, sources, builder);
                builder.success(param, param.parse(ctx, value));
            } catch (CommandMiddlewareException ex) {
                builder.fail(param, CommandInputArgument.ArgumentFailureType.INVALID);
                if ( failure == null ) {
                    failure = ex.handler();
                }
            } catch (Exception ex) {
                builder.fail(param, CommandInputArgument.ArgumentFailureType.INVALID);
                if ( failure == null ) {
                    failure = () -> { throw new RuntimeException("Error occured during argument parsing.", ex); };
                }
            }
        }

        // copy other values
        builder.withCursor(input.cursor());
        builder.withExcess(input.excess());

        CommandContext ctx = context(source, sources, builder);
        return new CommandDelegate(ctx, executor, failure);
    }

    @Override
    public List<Suggestion> suggestions(Object source, CommandInput input) {
        CommandDelegate delegate = prepare(source, input);
        if ( delegate.context().input().cursor() == null ) {
            return List.of();
        }

        CommandParameter param = Arrays.stream(parameters)
                .filter(p -> p.equals(delegate.context().input().cursor()))
                .findFirst().orElseThrow();

        String str = (String) input.argument(param);
        return param.suggestions(delegate.context(), str);
    }

    @Override
    public boolean available(Object source) {
        return condition == null || condition.test(source);
    }
}
