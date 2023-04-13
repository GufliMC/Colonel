package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.command.builder.CommandContextBuilder;
import com.guflimc.colonel.common.command.handler.CommandHandler;
import com.guflimc.colonel.common.command.handler.CommandParameter;

import java.util.*;
import java.util.stream.Collectors;

public class CommandDispatcher {

    private final CommandDispatcherContext context = new CommandDispatcherContext();
    private final Collection<CommandHandler> handlers = new HashSet<>();

    public void register(CommandHandler handler) {
        handlers.add(handler);
    }

    public void unregister(CommandHandler handler) {
        handlers.remove(handler);
    }

    public CommandDispatcherContext context() {
        return context;
    }

    public Collection<CommandHandler> handlers() {
        return Collections.unmodifiableCollection(handlers);
    }

    //

    public void dispatch(Object commandSource, String input) {

        // parse
        String[] parts = CommandParser.of(input).parse();
        Command command = new Command(commandSource, parts);

        // find handler
        MatchResult match = match(parts);
        if (match.exact) {
            dispatch(command, match.handler);
            return;
        }

        System.out.println("No exact match found.");

        // TODO send help message
    }

    private void dispatch(Command command, CommandHandler handler) {
        CommandContext context = CommandContextBuilder.of(this.context)
                .withCommand(command)
                .withHandler(handler)
                .build();

        // parse arguments
        String[] arguments = Arrays.copyOfRange(command.input(), handler.literals().length, command.input().length);
        List<CommandParameter<?>> indexed = Arrays.asList(handler.parameters());
        Set<CommandParameter<?>> unparsed = new HashSet<>(indexed);

        while (!unparsed.isEmpty()) {
            CommandParameter<?> parameter = unparsed.stream()
                    .filter(cp -> context.parsed.keySet().containsAll(cp.dependencies()))
                    .findFirst().orElseThrow(() -> new IllegalStateException("Circular dependency detected."));

            int index = indexed.indexOf(parameter);
            Object parsed = parameter.parse(context, arguments[index]);
            context.parsed.put(parameter, parsed);

            unparsed.remove(parameter);
        }

        handler.invoke(context);
    }

    private MatchResult match(String[] input) {
        Collection<CommandHandler> subset = handlers;
        Collection<CommandHandler> match = new HashSet<>();

        // find matching command handlers
        for (int i = 0; i < input.length; i++) {
            int index = i;
            Set<CommandHandler> current = subset.stream()
                    .filter(h -> h.literals().length > index)
                    .filter(h -> h.literals()[index].equalsIgnoreCase(input[index]))
                    .collect(Collectors.toSet());

            if ( current.isEmpty() ) {
                break;
            }

            subset = current;
            subset.stream()
                    .filter(h -> h.literals().length == index + 1)
                    .forEach(match::add);
        }

        if (match.isEmpty()) {
            // for literal suggestion
            return new MatchResult(best(subset, input), false);
        }

        Set<CommandHandler> perfect = match.stream()
                .filter(h -> h.literals().length + h.parameters().length == input.length)
                .collect(Collectors.toSet());

        if (perfect.isEmpty()) {
            // for usage suggestion
            return new MatchResult(best(match, input), false);
        }

        if (perfect.size() == 1) {
            return new MatchResult(perfect.iterator().next(), true);
        }

        throw new IllegalStateException("There are multiple commands registered with the same amount of literals and parameters.");
    }

    private CommandHandler best(Collection<CommandHandler> subset, String[] input) {
        // TODO levenshtein distance
        return subset.iterator().next();
    }

    private record MatchResult(CommandHandler handler, boolean exact) {
    }
}
