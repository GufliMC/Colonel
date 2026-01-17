package com.gufli.colonel.hytale;

import com.gufli.brick.i18n.hytale.localization.HytaleLocalizer;
import com.gufli.colonel.annotation.AnnotationColonel;
import com.gufli.colonel.common.dispatch.definition.CommandDefinition;
import com.gufli.colonel.common.dispatch.definition.CommandNode;
import com.gufli.colonel.common.dispatch.suggestion.Suggestion;
import com.gufli.colonel.common.dispatch.tree.CommandHandler;
import com.gufli.colonel.common.exception.CommandFailure;
import com.gufli.colonel.common.exception.CommandNotFoundFailure;
import com.gufli.colonel.common.exception.CommandPrepareParameterFailure;
import com.gufli.colonel.common.safe.SafeCommandContext;
import com.gufli.colonel.common.safe.SafeCommandHandlerBuilder;
import com.gufli.colonel.common.safe.SafeCommandParameterBuilder;
import com.gufli.colonel.hytale.annotations.command.CommandHelp;
import com.gufli.colonel.hytale.annotations.command.Permission;
import com.gufli.colonel.hytale.annotations.parameter.ParameterHelp;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HytaleColonel extends AnnotationColonel<CommandSender> {

    private final static Color RED = new Color(0xFF5555);
    private final static Color DARK_RED = new Color(0xAA0000);

    private final HytaleLocalizer localizer;
    private @Nullable BiConsumer<CommandSender, CommandFailure> errorHandler;

    private final JavaPlugin plugin;

    public HytaleColonel(@NotNull JavaPlugin plugin, @Nullable HytaleLocalizer localizer) {
        super(CommandSender.class);
        this.plugin = plugin;
        this.localizer = localizer;

        registerAll(new HytaleArguments(this, plugin));

        registry().registerSourceMapper(PlayerRef.class, source -> {
            if (source instanceof Player player) {
                var ref = player.getReference();
                if ( ref == null ) {
                    return null;
                }

                var store = ref.getStore();
                return store.getComponent(ref, PlayerRef.getComponentType());
            }
            return null;
        });

        registry().registerSourceMapper(World.class, source -> {
            if (source instanceof Player player) {
                return player.getWorld();
            }
            return null;
        });
    }

    public HytaleColonel(@NotNull JavaPlugin plugin) {
        this(plugin, null);
    }

    public void init() {
        var nodes = this.tree();

        for ( CommandNode node : nodes ) {
            HytaleCommand command = convert(node);
            plugin.getCommandRegistry().registerCommand(command);
        }
    }

    private HytaleCommand convert(CommandNode node) {
        HytaleCommand command;

        if ( node.definitions().size() == 1 ) {
            var definition = node.definitions().iterator().next();
            command = new HytaleCommand(this, node.name(), definition);
        }
        else {
            CommandDefinition empty = node.definitions().stream()
                    .filter(def -> def.parameters().length == 0)
                    .findFirst()
                    .orElse(null);

            if ( empty != null ) {
                command = new HytaleCommand(this, node.name(), empty);
            }
            else {
                command = new HytaleCommand(this, node.name());
            }

            for (CommandDefinition definition : node.definitions()) {
                if (definition.parameters().length == 0) {
                    continue;
                }

                HytaleCommand variant = new HytaleCommand(this, definition);
                command.addUsageVariant(variant);
            }
        }

        for ( CommandNode child : node.children() ) {
            HytaleCommand subCommand = convert(child);
            command.addSubCommand(subCommand);
        }

        return command;
    }

    //

    @Override
    protected void buildCommand(@NotNull Method method,
                                @NotNull Map<Parameter, Function<SafeCommandContext<CommandSender>, Object>> suppliers,
                                @NotNull SafeCommandHandlerBuilder<CommandSender> builder) {
        super.buildCommand(method, suppliers, builder);

        Permission permissionConf = method.getAnnotation(Permission.class);
        if (permissionConf != null) {
            builder.condition(s -> s.hasPermission(replacePlaceholders(permissionConf.value())));
            builder.property("permission", permissionConf.value());
        }

        CommandHelp commandHelpConf = method.getAnnotation(CommandHelp.class);
        if (commandHelpConf != null) {
            builder.property("description", commandHelpConf.description());
        }
    }

    @Override
    protected void buildParameter(@NotNull Parameter parameter, @NotNull Map<Parameter, Function<SafeCommandContext<CommandSender>, Object>> suppliers, @NotNull SafeCommandParameterBuilder<CommandSender> builder) {
        super.buildParameter(parameter, suppliers, builder);

        ParameterHelp parameterHelpConf = parameter.getAnnotation(ParameterHelp.class);
        if (parameterHelpConf != null) {
            builder.property("description", parameterHelpConf.description());
            builder.property("type", parameterHelpConf.type());
            builder.property("examples", parameterHelpConf.examples());
        }
    }

    @Override
    public void dispatch(CommandSender source, String input) {
        try {
            super.dispatch(source, input);
        } catch (CommandFailure failure) {
            handle(source, failure);
        }
    }

    @Override
    public List<Suggestion> suggestions(CommandSender source, String input, int cursor) {
        try {
            return super.suggestions(source, input, cursor);
        } catch (CommandFailure failure) {
            handle(source, failure);
        }
        return List.of();
    }

    //

    public void setErrorHandler(@NotNull BiConsumer<CommandSender, CommandFailure> errorHandler) {
        this.errorHandler = errorHandler;
    }

    //

    private void handle(CommandSender source, CommandFailure failure) {
        if (errorHandler != null) {
            errorHandler.accept(source, failure);
            return;
        }

        // USER FACING ERRORS

        if (failure instanceof CommandNotFoundFailure) {
            sendMessage(
                    source,
                    "cmderr.command-not-found",
                    Message.raw("Command not found: ").color(RED).insert(Message.raw("{0}").color(DARK_RED)),
                    failure.command()
            );
            return;
        }

        if (failure instanceof CommandPrepareParameterFailure pf) {
            if (pf.input() == null) {
                sendMessage(
                        source,
                        "cmderr.parameter-is-missing",
                        Message.raw("The parameter").color(RED)
                                .insert(Message.raw("{0}").color(DARK_RED))
                                .insert(Message.raw(" is missing. Expected syntax: ").color(RED))
                                .insert(Message.raw("{1}").color(DARK_RED)),
                        pf.parameter().name(),
                        pf.path() + " " + pf.definition().toString()
                );
                return;
            }
            if (pf.getCause() instanceof IllegalArgumentException) {
                sendMessage(
                        source,
                        "cmderr.parameter-invalid-value",
                        Message.raw("The value ").color(RED)
                                .insert(Message.raw("{0}").color(DARK_RED))
                                .insert(Message.raw(" is invalid for parameter ").color(RED))
                                .insert(Message.raw("{1}").color(DARK_RED))
                                .insert(Message.raw(".").color(RED)),
                        pf.input(),
                        pf.parameter().name()
                );
                return;
            }
        }

        // INTERNAL ERRORS FOR THE DEVELOPER
        sendMessage(
                source,
                "cmderr.generic",
                Message.raw("An unexpected error occured, check the console for more information.").color(RED)
        );

        if (failure.getCause() != null) {
            failure.getCause().printStackTrace();
        }
    }

    //

    void sendMessage(CommandSender source, String i18n, Message fallback, Object... args) {
        if (this.localizer != null) {
            localizer.send(source, i18n, args);
            return;
        }

        for (int i = 0; i < args.length; i++) {
            fallback = fallback.param(i + "", args[i].toString());
        }
        source.sendMessage(fallback);
    }

}