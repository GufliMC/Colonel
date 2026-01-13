package com.gufli.colonel.minecraft.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

class PaperBrigadierListener implements Listener {

    private final PaperColonel colonel;

    PaperBrigadierListener(PaperColonel colonel) {
        this.colonel = colonel;
    }

    @EventHandler
    public void onRegister(CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        // TODO
    }

}