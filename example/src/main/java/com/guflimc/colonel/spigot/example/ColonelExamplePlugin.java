package com.guflimc.colonel.spigot.example;

import com.guflimc.colonel.spigot.api.SpigotColonelManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ColonelExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SpigotColonelManager manager = new SpigotColonelManager(this);
        manager.register(new ColonelExampleCommands());

        getLogger().info("Enabled " + getName() + " v" + getDescription().getVersion());
    }
}
