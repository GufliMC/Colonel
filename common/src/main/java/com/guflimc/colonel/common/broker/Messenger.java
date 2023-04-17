package com.guflimc.colonel.common.broker;

import com.guflimc.colonel.common.suggestion.Suggestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Messenger {

    private final static String SPACE = Pattern.quote(" ");

    private final List<Node> nodes = new ArrayList<>();

    //

    public void register(String path, Handler handler) {
        if ( path.length() == 0 ) {
            throw new IllegalArgumentException("The path must not be empty.");
        }

        String[] args = path.split(SPACE);

        Node node = null;
        List<Node> nodes = this.nodes;
        for (String name : args) {
            node = nodes.stream().filter(n -> n.name().equalsIgnoreCase(name))
                    .findFirst().orElse(null);
            if (node != null) {
                nodes = node.edges();
                continue;
            }

            node = new Node(name);
            nodes.add(node);
            nodes = node.edges();
        }

        Objects.requireNonNull(node);
        node.mediator().addHandler(handler);
    }

    //

    public boolean apply(Object source, String input) {
        String[] args = input.split(SPACE);
        return apply(source, args, nodes);
    }

    private boolean apply(Object source, String[] input, List<Node> nodes) {
        if ( input.length == 0 ) {
            return false;
        }

        for ( Node node : nodes ) {
            if ( !node.name().equalsIgnoreCase(input[0]) ) {
                continue;
            }

            if ( apply(source, Arrays.copyOfRange(input, 1, input.length), node.edges()) ) {
                return true;
            }

            if ( node.mediator() == null ) {
                return false;
            }

            return node.mediator().apply(source, String.join(" ", input));
        }
        return false;
    }

    //

    public List<Suggestion> suggestions(Object source, String input) {
        return List.of();
    }

    private List<Suggestion> suggestions(Object source, String[] input, List<Node> nodes) {
        if ( input.length == 0 ) {
            return List.of();
        }

        for ( Node node : nodes ) {
            if ( !node.name().equalsIgnoreCase(input[0]) ) {
                continue;
            }

            List<Suggestion> suggestions = suggestions(source, Arrays.copyOfRange(input, 1, input.length), node.edges());

            if ( node.mediator() == null ) {
                return suggestions;
            }

            suggestions = new ArrayList<>(suggestions);
            suggestions.addAll(node.mediator().suggestions(source, String.join(" ", input)));
            return suggestions;
        }

        return List.of();
    }
}
