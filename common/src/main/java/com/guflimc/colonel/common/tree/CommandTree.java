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
        return apply(source, input.split(SPACE), input.length(), nodes);
    }

    private boolean apply(Object source, String[] input, int cursor, List<CommandTreeNode> nodes) {
        return recursive(source, input, cursor, nodes, (s, i, c, n) -> n.apply(s, String.join(" ", i)));
    }

    //

    public List<Suggestion> suggestions(Object source, String input, int cursor) {
        if ( cursor > input.length() ) {
            throw new IllegalArgumentException("The cursor must not exceed the input length.");
        }
        return suggestions(source, input.split(SPACE), cursor, nodes);
    }

    private List<Suggestion> suggestions(Object source, String[] input, int cursor, List<CommandTreeNode> nodes) {
        // suggest command path
        List<Suggestion> suggestions = new ArrayList<>(suggestions(input, cursor));

        // suggest arguments
        recursive(source, input, cursor, nodes, (s, j, c, n) -> {
            suggestions.addAll(n.suggestions(s, String.join(" ", j), c));
            return false;
        });
        return suggestions;
    }

    private List<Suggestion> suggestions(String[] input, int cursor) {
        List<Suggestion> suggestions = new ArrayList<>();

        // argcursor = which index of input contains the cursor, may exceed bounds of input
        int length = 0, argcursor = 0;
        for ( int i = 0; i < input.length; i++ ) {
            length += input[i].length() + 1;
            if ( cursor <= length ) {
                argcursor = cursor == length ? i + 1 : i;
                break;
            }
        }

        // traverse tree until node that matches with the argcursor
        List<CommandTreeNode> pool = nodes;
        CommandTreeNode node;
        for ( int i = 0; i < Math.min(input.length, argcursor); i++ ) {
            int index = i;
            node = pool.stream().filter(n -> n.name().equalsIgnoreCase(input[index]))
                    .findFirst().orElse(null);
            if ( node == null ) {
                return List.of(); // no match found for input before cursor
            }

            pool = node.children();
        }

        // get suggestions from the children of the parent node of the argcursor
        String prefix = argcursor < input.length ? input[argcursor].toLowerCase() : "";
        pool.stream().map(CommandTreeNode::name)
                .filter(name -> name.toLowerCase().startsWith(prefix))
                .forEach(name -> suggestions.add(new Suggestion(name)));

        return suggestions;
    }

    //

    private boolean recursive(Object source, String[] input, int cursor, List<CommandTreeNode> nodes, RecursiveHandler run) {
        if ( input.length == 0 || cursor < 0 ) {
            return false;
        }

        for ( CommandTreeNode node : nodes ) {
            if ( !node.name().equalsIgnoreCase(input[0]) ) {
                continue;
            }

            int nc = cursor - input[0].length() - Math.min(1, input.length - 1); // count space only if there is more input
            String[] ni = Arrays.copyOfRange(input, 1, input.length);
            if ( recursive(source, ni, nc, node.children(), run) ) {
                return true;
            }

            return run.apply(source, ni, nc, node); // execute for current node
        }
        return false;
    }

    @FunctionalInterface
    private interface RecursiveHandler {
        boolean apply(Object source, String[] input, int cursor, CommandTreeNode node);
    }
}
