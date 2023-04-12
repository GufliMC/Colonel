package com.guflimc.colonel.common.builder;

import com.guflimc.colonel.common.ColonelConfig;
import com.guflimc.colonel.common.annotation.command.Permission;
import com.guflimc.colonel.common.annotation.command.PermissionsLogic;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class AnnotatedCommandBuilder<S> {

    private final ColonelConfig<S> config;

    private Permission[] permissions = new Permission[0];
    private PermissionsLogic permissionsLogic = null;

    private final List<String[]> literals = new ArrayList<>();
    private final List<AnnotatedArgumentBuilder<S, ?>> arguments = new ArrayList<>();

    private Command<S> executor;

    public AnnotatedCommandBuilder(@NotNull ColonelConfig<S> config) {
        this.config = config;
    }

    //

    public AnnotatedCommandBuilder<S> withPermissions(@NotNull Permission[] permissions) {
        this.permissions = permissions;
        return this;
    }

    public AnnotatedCommandBuilder<S> withPermissionsLogic(@NotNull PermissionsLogic permissionsLogic) {
        this.permissionsLogic = permissionsLogic;
        return this;
    }

    public AnnotatedCommandBuilder<S> withLiterals(@NotNull String literals) {
        return withLiterals(literals.split(Pattern.quote(" ")));
    }

    public AnnotatedCommandBuilder<S> withLiterals(@NotNull String[] literals) {
        if (literals.length == 0) {
            throw new IllegalArgumentException("Literals must not be empty.");
        }
        this.literals.add(literals);
        return this;
    }

    public AnnotatedCommandBuilder<S> withArgument(@NotNull AnnotatedArgumentBuilder<S, ?> argument) {
        this.arguments.add(argument);
        return this;
    }

    public <T> AnnotatedCommandBuilder<S> withArgument(@NotNull String name, @NotNull Consumer<AnnotatedArgumentBuilder<S, T>> consumer) {
        AnnotatedArgumentBuilder<S, T> argument = new AnnotatedArgumentBuilder<>(config, name);
        consumer.accept(argument);
        return withArgument(argument);
    }

    public AnnotatedCommandBuilder<S> withExecutor(@NotNull Command<S> executor) {
        this.executor = executor;
        return this;
    }

    //

    public void register(RootCommandNode<S> root) {
        if (executor == null) {
            throw new IllegalStateException("Executor must be set.");
        }
        if (literals.isEmpty()) {
            throw new IllegalStateException("Literals must be set.");
        }

        // literals
        for (String[] literals : this.literals) {
            List<ArgumentBuilder<S, ?>> tree = new ArrayList<>();

            for (String literal : literals) {
                tree.add(literal(literal));
            }

            // arguments
            for (AnnotatedArgumentBuilder<S, ?> argument : this.arguments) {
                tree.add(argument.build());
            }

            // exit node
            ArgumentBuilder<S, ?> exit = tree.get(tree.size() - 1);

            // permissions
            if (permissions.length > 0 && config.permissionTester() != null) {
                PermissionsLogic.LogicalGate gate = PermissionsLogic.LogicalGate.AND;
                if ( permissionsLogic != null ) {
                    gate = permissionsLogic.value();
                }

                exit.requires(requires(gate, permissions));
            }

            // execution
            exit.executes(executor);

            // link nodes together
            for (int i = tree.size() - 1; i > 0; i--) {
                tree.get(i - 1).then(tree.get(i));
            }

            // register
            CommandNode<S> node = tree.get(0).build();
            root.addChild(node);

            // debug
//            StringBuilder result = new StringBuilder();
//            while ( true ) {
//                result.append(node instanceof ArgumentCommandNode ? "<" + node.getName() + ">" : node.getName()).append(" ");
//                if ( node.getChildren().isEmpty() ) break;
//                node = node.getChildren().iterator().next();
//            }
//            System.out.printf("Registered command: %s%n", result);
//            if ( permissions != null ) Arrays.stream(permissions).forEach(s -> System.out.println(s.value()));
        }
    }

    //

    private Predicate<S> requires(PermissionsLogic.LogicalGate gate, Permission[] permissions) {
        return source -> {
            int match = (int) Arrays.stream(permissions).filter(permission(source)).count();
            return gate.test(match, permissions.length);
        };
    }

    private Predicate<Permission> permission(S source) {
        return permission -> {
            if (permission.invert()) {
                return !config.permissionTester().test(source, permission.value());
            }
            return config.permissionTester().test(source, permission.value());
        };
    }

}
