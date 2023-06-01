package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.dispatch.definition.CommandDefinition;
import com.guflimc.colonel.common.dispatch.parser.CommandInput;
import com.guflimc.colonel.common.dispatch.parser.CommandInputArgument;
import com.guflimc.colonel.common.dispatch.parser.CommandInputBuilder;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.exception.CommandCompleteFailure;
import com.guflimc.colonel.common.exception.CommandFailure;
import com.guflimc.colonel.common.exception.CommandPrepareParameterFailure;
import com.guflimc.colonel.common.exception.CommandPrepareSourceFailure;
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
        CommandFailure failure = null;

        // sources
        Object[] sources = new Object[mappers.length];
        for (int i = 0; i < mappers.length; i++) {
            CommandSourceMapper csm = mappers[i];
            try {
                sources[i] = csm.map(source);
            } catch (Throwable f) {
                failure = new CommandPrepareSourceFailure(f).withSourceMapperIndex(i);
            }
        }

        // arguments
        for (CommandParameter param : parameters) {
            // missing error
            if (input.failure(param)) {
                builder.fail(param, input.error(param));
                if (failure == null) {
                    failure = new CommandPrepareParameterFailure(null)
                            .withParameter(param);
                }
                continue;
            }

            // parse value
            String value = (String) input.argument(param);
            try {
                CommandContext ctx = context(source, sources, builder);
                Object parsed = param.parse(ctx, value);
                if (parsed instanceof FailureHandler f) {
                    throw f; // TODO remove this, only here for backwards compatibility
                }

                builder.success(param, param.parse(ctx, value));
            } catch (Throwable f) {
                builder.fail(param, CommandInputArgument.ArgumentFailureType.INVALID);
                if (failure == null) {
                    failure = new CommandPrepareParameterFailure(f)
                            .withParameter(param)
                            .withInput(value);
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
        if (delegate.context().input().cursor() == null) {
            return List.of();
        }

        CommandParameter param = Arrays.stream(parameters)
                .filter(p -> p.equals(delegate.context().input().cursor()))
                .findFirst().orElseThrow();

        String str = (String) input.argument(param);
        try {
            return param.suggestions(delegate.context(), str);
        } catch (Throwable t) {
            throw new CommandCompleteFailure(t)
                    .withParameter(param)
                    .withInput(str);
        }
    }

    @Override
    public boolean available(Object source) {
        return condition == null || condition.test(source);
    }
}
