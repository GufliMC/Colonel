package com.guflimc.colonel.common.builder;

import com.guflimc.colonel.common.annotation.CommandPermissions;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class AnnotatedCommandBuilder<S> {

    private CommandPermissions permissions = null;
    private final List<String[]> literals = new ArrayList<>();
    private final List<AnnotatedArgumentBuilder<S, ?>> arguments = new ArrayList<>();

    public AnnotatedCommandBuilder<S> withPermissions(CommandPermissions permissions) {
        this.permissions = permissions;
        return this;
    }

    public AnnotatedCommandBuilder<S> withLiterals(String literals) {
        return withLiterals(literals.split(Pattern.quote(" ")));
    }

    public AnnotatedCommandBuilder<S> withLiterals(String[] literals) {
        if ( literals.length == 0 ) {
            throw new IllegalArgumentException("Literals must not be empty.");
        }
        this.literals.add(literals);
        return this;
    }

    public AnnotatedCommandBuilder<S> withArgument(AnnotatedArgumentBuilder<S, ?> argument) {
        this.arguments.add(argument);
        return this;
    }

    public <T> AnnotatedCommandBuilder<S> withArgument(Parameter parameter, Consumer<AnnotatedArgumentBuilder<S, T>> consumer) {
        AnnotatedArgumentBuilder<S, T> argument = new AnnotatedArgumentBuilder<>(parameter);
        consumer.accept(argument);
        return withArgument(argument);
    }

    public LiteralCommandNode<S> build() {
        return null; // TODO
    }

}
