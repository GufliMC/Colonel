package com.guflimc.colonel.spigot.example;

import com.guflimc.colonel.common.annotation.command.Command;
import com.guflimc.colonel.common.annotation.command.parameter.CommandSource;
import org.bukkit.entity.Player;

public class ColonelExampleCommands {

    @Command("level get")
    public void getLevel(@CommandSource Player player) {
        player.sendMessage("Your level is " + player.getLevel() + ".");
    }

    @Command("level set")
    public void setAge(@CommandSource Player player, int level) {
        player.setExp(0);
        player.setLevel(level);
        player.sendMessage("Your level has changed to " + level + ".");
    }

}
