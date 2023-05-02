package com.guflimc.colonel.common.ext;

import com.guflimc.colonel.common.dispatch.definition.CommandDefinition;
import com.guflimc.colonel.common.dispatch.parser.CommandInput;
import com.guflimc.colonel.common.dispatch.parser.CommandInputArgument;
import com.guflimc.colonel.common.dispatch.parser.CommandInputBuilder;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ExtCommandHandler extends com.guflimc.colonel.common.dispatch.tree.CommandHandler {

    private final ExtCommandParameter[] parameters;
    private final ExtCommandExecutor executor;
    private final Predicate<Object> condition;
    private final ExtCommandSourceMapper[] mappers;

    ExtCommandHandler(@NotNull ExtCommandParameter[] parameters,
                             @NotNull ExtCommandExecutor executor,
                             @NotNull ExtCommandSourceMapper[] mappers,
                             @NotNull Predicate<Object> condition) {
        super(new CommandDefinition(parameters));
        this.parameters = parameters;
        this.executor = executor;
        this.mappers = mappers;
        this.condition = condition;
    }

    private ExtCommandContext context(Object source, CommandInputBuilder b) {
        Supplier<Object>[] suppliers = Arrays.stream(mappers)
                .map(m -> (Supplier<Object>) () -> m.map(source))
                .toArray(Supplier[]::new);

        return new ExtCommandContext(b.build(), source, suppliers);
    }

    @Override
    public ExtCommandDelegate prepare(Object source, CommandInput input) {
        CommandInputBuilder builder = CommandInputBuilder.builder();
        Runnable failure = null;

        for ( ExtCommandParameter param : parameters ) {
            // missing error
            if ( input.failure(param) ) {
                builder.fail(param, input.error(param));
                if ( failure == null ) failure = () -> { throw new IllegalArgumentException("Missing argument: " + param.name()); }; // only first error
                continue;
            }

            // parse value
            String value = (String) input.argument(param);
            try {
                ExtCommandContext ctx = context(source, builder);
                Argument arg = param.parse(ctx, value);

                if ( arg instanceof Argument.ArgumentSuccess as) {
                    builder.success(param, as.value);
                } else if ( arg instanceof Argument.ArgumentFailure af ) {
                    builder.fail(param, CommandInputArgument.ArgumentFailureType.INVALID);
                    if ( failure == null ) failure = af.runnable; // only first error
                }
            } catch (Exception ex) {
                builder.fail(param, CommandInputArgument.ArgumentFailureType.INVALID);
                if ( failure == null ) failure = () -> { throw new RuntimeException("Error occured during argument parsing.", ex); }; // only first error
            }
        }

        // copy other values
        builder.withCursor(input.cursor());
        builder.withExcess(input.excess());

        ExtCommandContext ctx = context(source, builder);
        return new ExtCommandDelegate(ctx, executor, failure);
    }

    @Override
    public List<Suggestion> suggestions(Object source, CommandInput input) {
        ExtCommandDelegate delegate = prepare(source, input);
        if ( delegate.context().input().cursor() == null ) {
            return List.of();
        }

        ExtCommandParameter param = Arrays.stream(parameters)
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
