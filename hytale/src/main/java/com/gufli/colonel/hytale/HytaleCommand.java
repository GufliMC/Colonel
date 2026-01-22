package com.gufli.colonel.hytale;

import com.gufli.colonel.common.dispatch.definition.CommandDefinition;
import com.gufli.colonel.common.dispatch.definition.CommandParameter;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HytaleCommand extends AbstractCommand {

    private final HytaleColonel colonel;

    public HytaleCommand(@NotNull HytaleColonel colonel, @NotNull String name) {
        super(name, "");
        this.colonel = colonel;
    }

    public HytaleCommand(@NotNull HytaleColonel colonel, @NotNull CommandDefinition definition) {
        super(definition.propertyAsString("description").orElse(""));
        this.colonel = colonel;
        setup(definition);
    }

    public HytaleCommand(@NotNull HytaleColonel colonel, @NotNull String name, @NotNull CommandDefinition definition) {
        super(name, definition.propertyAsString("description").orElse(""));
        this.colonel = colonel;
        setup(definition);
    }

    //

    private void setup(@NotNull CommandDefinition definition) {
        this.setAllowsExtraArguments(true);

        String permission = definition.propertyAsString("permission").filter(p -> !p.isEmpty()).orElse(null);
        this.requirePermission(Objects.requireNonNullElse(permission, ""));

        if ( permission == null || permission.isBlank() ) {
            this.setPermissionGroups(GameMode.Adventure.toString(), GameMode.Creative.toString());
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

    //

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
