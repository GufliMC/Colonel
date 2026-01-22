package com.gufli.colonel.hytale;

import com.gufli.brick.i18n.common.time.DurationParser;
import com.gufli.colonel.annotation.annotations.Completer;
import com.gufli.colonel.annotation.annotations.Parser;
import com.gufli.colonel.annotation.annotations.parameter.Input;
import com.gufli.colonel.annotation.annotations.parameter.Source;
import com.gufli.colonel.common.build.FailureHandler;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class HytaleArguments {

    private final HytaleColonel colonel;
    private final JavaPlugin plugin;

    public HytaleArguments(@NotNull HytaleColonel colonel, @NotNull JavaPlugin plugin) {
        this.colonel = colonel;
        this.plugin = plugin;
    }

    // PLAYER

    @Parser(value = "player", type = PlayerRef.class)
    public Object playerParser(@Source CommandSender source, @Input String input) {
        PlayerRef player = Universe.get().getPlayers().stream()
                .filter(p -> p.getUsername().equalsIgnoreCase(input))
                .findFirst().orElse(null);
        if (player != null) {
            return player;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmderr.args.player-not-online", Message.raw("Player not found: {0}"), input));
    }

    @Completer(value = "player", type = PlayerRef.class)
    public List<String> playerCompleter() {
        return Universe.get().getPlayers().stream()
                .map(PlayerRef::getUsername).toList();
    }

    // WORLD

    @Parser(value = "world", type = World.class)
    public Object worldParser(@Source CommandSender source, @Input String input) {
        World world = Universe.get().getWorlds().values().stream()
                .filter(w -> w.getName().equalsIgnoreCase(input))
                .findFirst().orElse(null);
        if (world != null) {
            return world;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmderr.args.world-not-exist", Message.raw("World does not exist: {0}"), input));
    }

    @Completer(value = "world", type = World.class)
    public List<String> worldCompleter() {
        return Universe.get().getWorlds().values().stream()
                .map(World::getName).toList();
    }

    // ITEM

    @Parser(value = "item", type = Item.class)
    public Object itemParser(@Source CommandSender source, @Input String input) {
        Item item = AssetRegistry.getAssetStore(Item.class).getAssetMap().getAssetMap().get(input);
        if (item != null) {
            return item;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmderr.args.item-not-exist", Message.raw("Item asset does not exist: {0}"), input));
    }

    @Completer(value = "item", type = Item.class)
    public List<String> itemCompleter() {
        return new ArrayList<>(AssetRegistry.getAssetStore(Item.class).getAssetMap().getAssetMap().keySet());
    }

    // DURATION

    @Parser(value = "duration", type = Duration.class)
    public Object durationParser(@Source CommandSender source, @Input String input) {
        Duration duration = DurationParser.parse(input);
        if ( !duration.isZero() ) {
            return duration;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmderr.args.invalid-duration", Message.raw("Duration format is invalid: {0}. Examples: '1d', '2h30m', '45s'"), input));
    }

}

