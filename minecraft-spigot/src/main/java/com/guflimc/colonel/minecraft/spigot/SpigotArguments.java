package com.guflimc.colonel.minecraft.spigot;

import com.guflimc.adventure.MixedLegacyComponentSerializer;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.colonel.annotation.annotations.Completer;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.ext.Argument;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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

    private final JavaPlugin plugin;
    private final BukkitAudiences audiences;

    public SpigotArguments(@NotNull JavaPlugin plugin, @NotNull BukkitAudiences audiences) {
        this.plugin = plugin;
        this.audiences = audiences;
    }

    private void sendError(CommandSender source, String i18n, String fallback, Object... args) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("BrickI18n")) {
            SpigotI18nAPI.get(plugin).send(source, "global:" + i18n, args);
            return;
        }

        String str = fallback;
        for (int i = 0; i < args.length; i++) {
            str = str.replace("{" + i + "}", args[i].toString());
        }
        source.sendMessage(ChatColor.RED + str);
    }

    // PLAYER

    @Parser(value = "player", type = Player.class)
    public Argument playerParser(@Source CommandSender source, String input) {
        Player player = plugin.getServer().getPlayer(input);
        if (player != null) {
            return Argument.success(player);
        } else {
            return Argument.fail(() -> sendError(source, "cmd.error.player.notfound",
                    "Player not found: {0}", input));
        }
    }

    @Completer(value = "player", type = Player.class)
    public List<String> playerCompleter() {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }

    // AUDIENCE

    @Parser(value = "audience", type = Audience.class)
    public Argument audienceParser(@Source CommandSender source, String input) {
        Player player = plugin.getServer().getPlayer(input);
        if (player != null) {
            return Argument.success(audiences.player(player));
        } else {
            return Argument.fail(() -> sendError(source, "cmd.error.player.notfound",
                    "Player not found: {0}", input));
        }
    }

    @Completer(value = "audience", type = Audience.class)
    public List<String> audienceCompleter() {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }

    // WORLD

    @Parser(value = "world", type = World.class)
    public Argument worldParser(@Source CommandSender source, String input) {
        World world = plugin.getServer().getWorld(input);
        if (world != null) {
            return Argument.success(world);
        } else {
            return Argument.fail(() -> sendError(source, "cmd.error.world.notfound",
                    "World not found: {0}", input));
        }
    }

    @Completer(value = "world", type = World.class)
    public List<String> worldCompleter() {
        return plugin.getServer().getWorlds().stream()
                .map(World::getName)
                .toList();
    }

    // MATERIAL

    @Parser(value = "material", type = Material.class)
    public Argument materialParser(@Source CommandSender source, String input) {
        Material material = Material.matchMaterial(input);
        if (material != null) {
            return Argument.success(material);
        } else {
            return Argument.fail(() -> sendError(source, "cmd.error.material.notfound",
                    "Material not found: {0}", input));
        }
    }

    @Completer(value = "material", type = Material.class)
    public List<String> materialCompleter(String input) {
        return Arrays.stream(Material.values())
                .map(m -> m.getKey().toString())
                .toList();
    }

    // SOUND

    @Parser(value = "sound", type = Sound.class)
    public Argument soundParser(@Source CommandSender source, String input) {
        Sound sound = Arrays.stream(Sound.values())
                .filter(s -> s.getKey().toString().equals(input))
                .findFirst().orElse(null);
        if (sound != null) {
            return Argument.success(sound);
        } else {
            return Argument.fail(() -> sendError(source, "cmd.error.sound.notfound",
                    "Sound not found: {0}", input));
        }
    }

    @Completer(value = "sound", type = Sound.class)
    public List<String> soundCompleter() {
        return Arrays.stream(Sound.values())
                .map(Enum::name)
                .toList();
    }

    // ENTITY TYPE

    @Parser(value = "entityType", type = EntityType.class)
    public Argument entityTypeParser(@Source CommandSender source, String input) {
        EntityType entityType = Arrays.stream(EntityType.values())
                .filter(s -> s.getKey().toString().equals(input))
                .findFirst().orElse(null);
        if (entityType != null) {
            return Argument.success(entityType);
        } else {
            return Argument.fail(() -> sendError(source, "cmd.error.entitytype.notfound",
                    "Entity type not found: {0}", input));
        }
    }

    @Completer(value = "entityType", type = EntityType.class)
    public List<String> entityTypeCompleter() {
        return Arrays.stream(EntityType.values())
                .map(e -> e.getKey().toString())
                .toList();
    }

    // POTION EFFECT TYPE

    @Parser(value = "potioneEffectType", type = PotionEffectType.class)
    public Argument potionEffectTypeParser(@Source CommandSender source, String input) {
        PotionEffectType potionEffectType = Arrays.stream(PotionEffectType.values())
                .filter(s -> s.getKey().toString().equals(input))
                .findFirst().orElse(null);
        if (potionEffectType != null) {
            return Argument.success(potionEffectType);
        } else {
            return Argument.fail(() -> sendError(source, "cmd.error.potioneffecttype.notfound",
                    "Potion effect type not found: {0}", input));
        }
    }

    @Completer(value = "potioneEffectType", type = PotionEffectType.class)
    public List<String> potionEffectTypeCompleter() {
        return Arrays.stream(PotionEffectType.values())
                .map(p -> p.getKey().toString())
                .toList();
    }

    // ENCHANTMENT

    @Parser(value = "enchantment", type = Enchantment.class)
    public Argument enchantmentParser(@Source CommandSender source, String input) {
        Enchantment enchantment = Arrays.stream(Enchantment.values())
                .filter(s -> s.getKey().toString().equals(input))
                .findFirst().orElse(null);
        if (enchantment != null) {
            return Argument.success(enchantment);
        } else {
            return Argument.fail(() -> sendError(source, "cmd.error.enchantment.notfound",
                    "Enchantment not found: {0}", input));
        }
    }

    @Completer(value = "enchantment", type = Enchantment.class)
    public List<String> enchantmentCompleter() {
        return Arrays.stream(Enchantment.values())
                .map(e -> e.getKey().toString())
                .toList();
    }

    // COMPONENT

    @Parser(value = "component", type = Component.class)
    public Argument componentParser(String input) {
        Component component = MixedLegacyComponentSerializer.deserialize(input);
        return Argument.success(component);
    }

    // COLOR

    @Parser(value = "color", type = Color.class)
    public Argument colorParser(String input) {
        Color color = Color.fromRGB(Integer.decode(input));
        return Argument.success(color);
    }

    @Parser(value = "color", type = java.awt.Color.class)
    public Argument intColorParser(String input) {
        return Argument.success(java.awt.Color.decode(input));
    }

    // TEXT COLOR

    @Parser(value = "textcolor", type = TextColor.class)
    public Argument adventureColorParser(String input) {
        NamedTextColor color = NamedTextColor.NAMES.value(input);
        if ( color != null ) {
            return Argument.success(color);
        }
        return Argument.success(TextColor.color(Integer.decode(input)));
    }

}
