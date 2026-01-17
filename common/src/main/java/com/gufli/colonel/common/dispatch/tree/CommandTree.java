package com.gufli.colonel.common.dispatch.tree;

import com.gufli.colonel.common.dispatch.suggestion.Suggestion;
import com.gufli.colonel.common.exception.CommandDispatchFailure;

import java.util.*;
import java.util.regex.Pattern;

public final class CommandTree {

    private final static String SPACE = Pattern.quote(" ");

    private final List<CommandTreeNode> nodes = new ArrayList<>();

    //

    public List<CommandTreeNode> nodes() {
        return Collections.unmodifiableList(nodes);
    }

    //

    public void register(String path, CommandHandler handler) {
        if (path.length() == 0) {
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
        return recursive(source, "", input, cursor, nodes, (s, p, i, c, n) -> {
            try {
                return n.apply(s, String.join(" ", i));
            } catch (CommandDispatchFailure e) {
                throw e.withPath(p);
            }
        });
    }

    //

    public List<Suggestion> suggestions(Object source, String input, int cursor) {
        if (cursor > input.length()) {
            throw new IllegalArgumentException("The cursor must not exceed the input length.");
        }
        return suggestions(source, input.split(SPACE), cursor, nodes);
    }

    private List<Suggestion> suggestions(Object source, String[] input, int cursor, List<CommandTreeNode> nodes) {
        // suggest command path
        List<Suggestion> suggestions = new ArrayList<>(suggestions(input, cursor));

        // suggest arguments
        recursive(source, "", input, cursor, nodes, (s, p, j, c, n) -> {
            try {
                suggestions.addAll(n.suggestions(s, String.join(" ", j), c));
                return false;
            } catch (CommandDispatchFailure e) {
                throw e.withPath(p);
            }
        });
        return suggestions;
    }

    private List<Suggestion> suggestions(String[] input, int cursor) {
        List<Suggestion> suggestions = new ArrayList<>();

        // argcursor = which index of input contains the cursor, may exceed bounds of input
        int length = 0, argcursor = 0;
        for (int i = 0; i < input.length; i++) {
            length += input[i].length() + 1;
            if (cursor <= length) {
                argcursor = cursor == length ? i + 1 : i;
                break;
            }
        }

        // traverse tree until node that matches with the argcursor
        List<CommandTreeNode> pool = nodes;
        CommandTreeNode node;
        for (int i = 0; i < Math.min(input.length, argcursor); i++) {
            int index = i;
            node = pool.stream().filter(n -> n.name().equalsIgnoreCase(input[index]))
                    .findFirst().orElse(null);
            if (node == null) {
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

    private boolean recursive(Object source, String path, String[] input, int cursor, List<CommandTreeNode> nodes, RecursiveHandler run) {
        if (input.length == 0 || cursor < 0) {
            return false;
        }

        for (CommandTreeNode node : nodes) {
            if (!node.name().equalsIgnoreCase(input[0])) {
                continue;
            }

            int nc = cursor - input[0].length() - 1;
            path = (path + " " + input[0]).stripLeading();
            String[] ni = Arrays.copyOfRange(input, 1, input.length);
            if (recursive(source, path, ni, nc, node.children(), run)) {
                return true;
            }

            return run.apply(source, path, ni, nc, node); // execute for current node
        }
        return false;
    }

    @FunctionalInterface
    private interface RecursiveHandler {
        boolean apply(Object source, String path, String[] input, int cursor, CommandTreeNode node);
    }
}
