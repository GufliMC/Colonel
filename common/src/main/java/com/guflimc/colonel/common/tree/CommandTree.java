package com.guflimc.colonel.common.tree;

import com.guflimc.colonel.common.suggestion.Suggestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class CommandTree {

    private final static String SPACE = Pattern.quote(" ");

    private final List<CommandTreeNode> nodes = new ArrayList<>();

    //

    public void register(String path, CommandHandler handler) {
        if ( path.length() == 0 ) {
            throw new IllegalArgumentException("The path must not be empty.");
        }

        String[] args = path.split(SPACE);

        CommandTreeNode node = null;
        List<CommandTreeNode> nodes = this.nodes;
        for (String name : args) {
            node = nodes.stream().filter(n -> n.name().equalsIgnoreCase(name))
                    .findFirst().orElse(null);
            if (node != null) {
                nodes = node.children();
                continue;
            }

            node = new CommandTreeNode(name);
            nodes.add(node);
            nodes = node.children();
        }

        Objects.requireNonNull(node);
        node.handlers().add(handler);
    }

    //

    public boolean apply(Object source, String input) {
        return apply(source, input.split(SPACE), nodes);
    }

    private boolean apply(Object source, String[] input, List<CommandTreeNode> nodes) {
        return recursive(source, input, nodes, (s, i, n) -> n.apply(s, String.join(" ", i)));
    }

    //

    public List<Suggestion> suggestions(Object source, String input) {
        return suggestions(source, input.split(SPACE), nodes);
    }

    private List<Suggestion> suggestions(Object source, String[] input, List<CommandTreeNode> nodes) {
        List<Suggestion> suggestions = new ArrayList<>();
        recursive(source, input, nodes, (s, i, n) -> {
            suggestions.addAll(n.suggestions(s, String.join(" ", i)));
            return false;
        });
        return suggestions;
    }

    //

    private boolean recursive(Object source, String[] input, List<CommandTreeNode> nodes, RecursiveHandler run) {
        if ( input.length == 0 ) {
            return false;
        }

        for ( CommandTreeNode node : nodes ) {
            if ( !node.name().equalsIgnoreCase(input[0]) ) {
                continue;
            }

            if ( recursive(source, Arrays.copyOfRange(input, 1, input.length), node.children(), run) ) {
                return true;
            }

            return run.apply(source, input, node); //node.apply(source, String.join(" ", input));
        }
        return false;
    }

    @FunctionalInterface
    private interface RecursiveHandler {
        boolean apply(Object source, String[] input, CommandTreeNode node);
    }
}
