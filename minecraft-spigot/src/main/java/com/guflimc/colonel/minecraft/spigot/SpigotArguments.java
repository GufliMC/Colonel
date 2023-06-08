package com.guflimc.colonel.minecraft.spigot;

import com.guflimc.adventure.MixedLegacyComponentSerializer;
import com.guflimc.colonel.annotation.annotations.Completer;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.build.FailureHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SpigotArguments {

    private final SpigotColonel colonel;

    private final JavaPlugin plugin;
    private final BukkitAudiences audiences;

    public SpigotArguments(@NotNull SpigotColonel colonel) {
        this.colonel = colonel;
        this.plugin = colonel.plugin;
        this.audiences = colonel.audiences;
    }

    // PLAYER

    @Parser(value = "player", type = Player.class)
    public Object playerParser(@Source CommandSender source, String input) {
        Player player = plugin.getServer().getPlayer(input);
        if (player != null) {
            return player;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmd.error.player.notfound", "Player not found: {0}", input));
    }

    @Completer(value = "player", type = Player.class)
    public List<String> playerCompleter() {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }

    // AUDIENCE

    @Parser(value = "audience", type = Audience.class)
    public Object audienceParser(@Source CommandSender source, String input) {
        Player player = plugin.getServer().getPlayer(input);
        if (player != null) {
            return audiences.player(player);
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmd.error.player.notfound", ChatColor.RED + "Player not found: {0}", input));
    }

    @Completer(value = "audience", type = Audience.class)
    public List<String> audienceCompleter() {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }

    // WORLD

    @Parser(value = "world", type = World.class)
    public Object worldParser(@Source CommandSender source, String input) {
        World world = plugin.getServer().getWorld(input);
        if (world != null) {
            return world;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmd.error.world.notfound", ChatColor.RED + "World not found: {0}", input));
    }

    @Completer(value = "world", type = World.class)
    public List<String> worldCompleter() {
        return plugin.getServer().getWorlds().stream()
                .map(World::getName)
                .toList();
    }

    // MATERIAL

    @Parser(value = "material", type = Material.class)
    public Object materialParser(@Source CommandSender source, String input) {
        Material material = Material.matchMaterial(input);
        if (material != null) {
            return material;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmd.error.material.notfound", ChatColor.RED + "Material not found: {0}", input));
    }

    @Completer(value = "material", type = Material.class)
    public List<String> materialCompleter(String input) {
        return Arrays.stream(Material.values())
                .map(m -> m.getKey().toString())
                .toList();
    }

    // SOUND

    @Parser(value = "sound", type = Sound.class)
    public Object soundParser(@Source CommandSender source, String input) {
        Sound sound = Arrays.stream(Sound.values())
                .filter(s -> s.getKey().toString().equals(input))
                .findFirst().orElse(null);
        if (sound != null) {
            return sound;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmd.error.sound.notfound", ChatColor.RED + "Sound not found: {0}", input));
    }

    @Completer(value = "sound", type = Sound.class)
    public List<String> soundCompleter() {
        return Arrays.stream(Sound.values())
                .map(Enum::name)
                .toList();
    }

    // ENTITY TYPE

    @Parser(value = "entityType", type = EntityType.class)
    public Object entityTypeParser(@Source CommandSender source, String input) {
        EntityType entityType = Arrays.stream(EntityType.values())
                .filter(s -> s.getKey().toString().equals(input))
                .findFirst().orElse(null);
        if (entityType != null) {
            return entityType;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmd.error.entitytype.notfound", ChatColor.RED + "Entity type not found: {0}", input));
    }

    @Completer(value = "entityType", type = EntityType.class)
    public List<String> entityTypeCompleter() {
        return Arrays.stream(EntityType.values())
                .map(e -> e.getKey().toString())
                .toList();
    }

    // POTION EFFECT TYPE

    @Parser(value = "potioneEffectType", type = PotionEffectType.class)
    public Object potionEffectTypeParser(@Source CommandSender source, String input) {
        PotionEffectType potionEffectType = Arrays.stream(PotionEffectType.values())
                .filter(s -> s.getKey().toString().equals(input))
                .findFirst().orElse(null);
        if (potionEffectType != null) {
            return potionEffectType;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmd.error.potioneffecttype.notfound", ChatColor.RED + "Potion effect type not found: {0}", input));
    }

    @Completer(value = "potioneEffectType", type = PotionEffectType.class)
    public List<String> potionEffectTypeCompleter() {
        return Arrays.stream(PotionEffectType.values())
                .map(p -> p.getKey().toString())
                .toList();
    }

    // ENCHANTMENT

    @Parser(value = "enchantment", type = Enchantment.class)
    public Object enchantmentParser(@Source CommandSender source, String input) {
        Enchantment enchantment = Arrays.stream(Enchantment.values())
                .filter(s -> s.getKey().toString().equals(input))
                .findFirst().orElse(null);
        if (enchantment != null) {
            return enchantment;
        }
        return FailureHandler.of(() -> colonel.sendMessage(source, "cmd.error.enchantment.notfound", ChatColor.RED + "Enchantment not found: {0}", input));
    }

    @Completer(value = "enchantment", type = Enchantment.class)
    public List<String> enchantmentCompleter() {
        return Arrays.stream(Enchantment.values())
                .map(e -> e.getKey().toString())
                .toList();
    }

    // COMPONENT

    @Parser(value = "component", type = Component.class)
    public Object componentParser(String input) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
    }

    // COLOR

    @Parser(value = "color", type = Color.class)
    public Object colorParser(String input) {
        return Color.fromRGB(Integer.decode(input));
    }

    @Parser(value = "color", type = java.awt.Color.class)
    public Object intColorParser(String input) {
        return java.awt.Color.decode(input);
    }

    // TEXT COLOR

    @Parser(value = "textcolor", type = TextColor.class)
    public Object adventureColorParser(String input) {
        NamedTextColor color = NamedTextColor.NAMES.value(input);
        if (color != null) {
            return color;
        }
        return TextColor.color(Integer.decode(input));
    }

}
