package com.guflimc.colonel.common.broker;

import java.util.ArrayList;
import java.util.List;

public final class Node {

    private final String name;
    private final List<Node> edges = new ArrayList<>(); // exposed because this may be modified internally
    private final Mediator mediator = new Mediator();

    public Node(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public List<Node> edges() {
        return edges;
    }

    public Mediator mediator() {
        return mediator;
    }

}
