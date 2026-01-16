package com.gufli.colonel.hytale;

import com.gufli.colonel.common.dispatch.definition.CommandDefinition;
import com.gufli.colonel.common.dispatch.definition.CommandParameter;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class HytaleCommand extends AbstractCommand {

    private final HytaleColonel colonel;

    private HytaleCommand(@NotNull HytaleColonel colonel, @NotNull String name) {
        super(name, null);
        this.colonel = colonel;
    }

    public HytaleCommand(@NotNull HytaleColonel colonel, @NotNull String[] paths, @NotNull CommandDefinition definition) {
        this(colonel, paths[0].split(" ")[0]);
        this.setAllowsExtraArguments(true);

        for ( int i = 1; i < paths.length; i++ ) {
            this.addAliases(paths[i].split(" ")[0]);
        }

        String[] literals = paths[0].split(" ");
        if (literals.length > 1) {
            this.add(Arrays.copyOfRange(literals, 1, literals.length), definition);
            return;
        }

        String permission = definition.propertyAsString("permission").filter(p -> !p.isEmpty()).orElse(null);
        if ( permission != null ) {
            this.requirePermission(permission);
        } else {
            this.requirePermission("");
        }

        for ( CommandParameter param : definition.parameters() ) {
            String description = definition.propertyAsString("parameters." + param.name() + ".description").orElse("");
            String type = definition.propertyAsString("parameters." + param.name() + ".type").orElse("");
            String usage = definition.propertyAsString("parameters." + param.name() + ".usage").orElse("");

            this.withRequiredArg(param.name(), description, new SingleArgumentType<String>(type, usage) {
                @Override
                public @Nullable String parse(String s, ParseResult parseResult) {
                    return s;
                }
            });
        }
    }

    public HytaleCommand(@NotNull HytaleColonel colonel, @NotNull String path, @NotNull CommandDefinition definition) {
        this(colonel, path.split(" ")[0]);
    }

    //

    public void add(@NotNull String path, @NotNull CommandDefinition definition) {
        this.add(path.split(" "), definition);
    }

    public void add(@NotNull String[] path, @NotNull CommandDefinition definition) {
        if ( path.length == 1 ) {
            var command = new HytaleCommand(colonel, path[0], definition);
            this.addSubCommand(command);
            return;
        }

        var command = new HytaleCommand(colonel, path[0]);
        command.add(Arrays.copyOfRange(path, 1, path.length), definition);
        this.addSubCommand(command);
    }

    @Override
    protected @Nullable CompletableFuture<Void> execute(@NotNull CommandContext ctx) {
        if ( ctx.isPlayer() ) {
            World world = ctx.senderAs(Player.class).getWorld();
            if ( world != null ) {
                return CompletableFuture.runAsync(() -> {
                    colonel.dispatch(ctx.sender(), ctx.getInputString());
                }, world);
            }
        }

        colonel.dispatch(ctx.sender(), ctx.getInputString());
        return CompletableFuture.completedFuture(null);
    }


}
