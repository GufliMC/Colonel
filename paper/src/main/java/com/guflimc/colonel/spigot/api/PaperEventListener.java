package com.guflimc.colonel.spigot.api;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

class PaperEventListener implements Listener {

    private final PaperColonelManager manager;

    PaperEventListener(PaperColonelManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onRegister(CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        LiteralCommandNode<CommandSender> node = manager.colonel.dispatcher().getRoot().getChildren().stream()
                .map(n -> (LiteralCommandNode<CommandSender>) n)
                .filter(n -> n.getLiteral().equals(event.getLiteral().getLiteral()))
                .findFirst().orElse(null);

        if ( node == null ) {
            return;
        }

        // TODO
    }

    private CommandNode<BukkitBrigadierCommandSource> clone(CommandNode<CommandSender> node) {
        throw new UnsupportedOperationException();
    }
}