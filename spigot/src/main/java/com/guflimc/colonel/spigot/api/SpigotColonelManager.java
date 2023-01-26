package com.guflimc.colonel.spigot.api;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.ColonelConfig;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SpigotColonelManager {

    final Colonel<CommandSender> colonel = new Colonel<>();
    final SimpleCommandMap commandMap;

    final JavaPlugin plugin;
    final List<ColonelCommand> commands = new ArrayList<>();

    public SpigotColonelManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new EventListener(), plugin);

        colonel.config().withPermmissionTester(CommandSender::hasPermission);

        try {
            commandMap = (SimpleCommandMap) plugin.getServer().getClass()
                    .getMethod("getCommandMap").invoke(plugin.getServer());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public ColonelConfig<CommandSender> config() {
        return colonel.config();
    }

    public void register(@NotNull Object container) {
        colonel.registerCommands(container);
        update();
    }

    protected void update() {
        for ( CommandNode<CommandSender> node : colonel.dispatcher().getRoot().getChildren() ) {
            LiteralCommandNode<CommandSender> lcn = (LiteralCommandNode<CommandSender>) node;
            if ( commands.stream().anyMatch(c -> c.getName().equalsIgnoreCase(lcn.getLiteral())) ) {
                continue;
            }

            ColonelCommand cmd = new ColonelCommand(this, lcn.getLiteral());
            commandMap.register(plugin.getName().toLowerCase(), cmd);
            commands.add(cmd);
        }
    }

    //

    private class EventListener implements Listener {

        @EventHandler
        public void onDisable(PluginDisableEvent event) {
            if (!event.getPlugin().equals(plugin)) {
                return;
            }

            colonel.unregisterAll();

            commands.forEach(cmd -> cmd.unregister(commandMap));
            commands.clear();
        }

    }

}
