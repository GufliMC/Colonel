package com.guflimc.colonel.spigot.api;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperColonelManager extends SpigotColonelManager {

    public PaperColonelManager(JavaPlugin plugin) {
        super(plugin);

        plugin.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
    }

    //

    private class EventListener implements Listener {


    }
}
