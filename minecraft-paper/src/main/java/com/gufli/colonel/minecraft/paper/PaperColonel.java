package com.gufli.colonel.minecraft.paper;

import com.gufli.colonel.minecraft.spigot.SpigotColonel;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperColonel extends SpigotColonel {

    public PaperColonel(JavaPlugin plugin) {
        super(plugin);

        plugin.getServer().getPluginManager().registerEvents(new PaperBrigadierListener(this), plugin);
    }

}
