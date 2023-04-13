package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.command.builder.CommandContextBuilder;
import com.guflimc.colonel.common.command.builder.CommandSyntaxBuilder;
import com.guflimc.colonel.common.command.syntax.CommandExecutor;
import com.guflimc.colonel.common.command.syntax.CommandSyntax;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandDispatcher {

    private final CommandDispatcherContext context = new CommandDispatcherContext();
    private final Map<CommandSyntax, CommandExecutor> syntaxes = new HashMap<>();

    public void register(CommandSyntax syntax, CommandExecutor executor) {
        syntaxes.put(syntax, executor);
    }

    public void register(Consumer<CommandSyntaxBuilder> cs, CommandExecutor executor) {
        CommandSyntaxBuilder builder = CommandSyntaxBuilder.of(context);
        cs.accept(builder);
        syntaxes.put(builder.build(), executor);
    }

    public void unregister(CommandSyntax syntax) {
        syntaxes.remove(syntax);
    }

    public CommandDispatcherContext context() {
        return context;
    }

    public Collection<CommandSyntax> syntaxes() {
        return Collections.unmodifiableCollection(syntaxes.keySet());
    }

    //

    public void dispatch(Object commandSource, String input) {

        // parse
        String[] parts = CommandParser.of(input).parse();
        Command command = new Command(commandSource, parts);

        // find syntax
        MatchResult match = match(parts);
        if (match.exact) {
            dispatch(command, match.syntax);
            return;
        }

        System.out.println("No exact match found.");

        // TODO send help message
    }

    private void dispatch(Command command, CommandSyntax syntax) {
        CommandContext context = CommandContextBuilder.of(this.context)
                .withCommand(command)
                .withSyntax(syntax)
                .build();

        // parse arguments
        String[] arguments = Arrays.copyOfRange(command.input(), syntax.literals().length, command.input().length);
        for ( int i = 0; i < syntax.parameters().length; i++ ) {
            Object parsed = syntax.parameters()[i].type().parse(context, arguments[i]);
            context.parsed.put(syntax.parameters()[i], parsed);
        }

        syntaxes.get(syntax).invoke(context);
    }

    private MatchResult match(String[] input) {
        Collection<CommandSyntax> subset = syntaxes.keySet();
        Collection<CommandSyntax> match = new HashSet<>();

        // find matching command handlers
        for (int i = 0; i < input.length; i++) {
            int index = i;
            Set<CommandSyntax> current = subset.stream()
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

        Set<CommandSyntax> perfect = match.stream()
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

    private CommandSyntax best(Collection<CommandSyntax> subset, String[] input) {
        // TODO levenshtein distance
        return subset.iterator().next();
    }

    private record MatchResult(CommandSyntax syntax, boolean exact) {
    }
}
