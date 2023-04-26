package com.guflimc.colonel.minecraft.spigot;

import com.guflimc.adventure.MixedLegacyComponentSerializer;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.colonel.common.build.Argument;
import com.guflimc.colonel.common.build.CommandHandlerBuilder;
import com.guflimc.colonel.common.suggestion.Suggestion;
import com.guflimc.colonel.common.tree.CommandHandler;
import com.guflimc.colonel.minecraft.common.MinecraftColonel;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpigotColonel extends MinecraftColonel<CommandSender> {

    private final JavaPlugin plugin;
    private final BukkitAudiences audiences;

    private final SimpleCommandMap commandMap;

    record RegisteredCommand(@NotNull String path, @NotNull CommandHandler handler, @NotNull SpigotCommand command) {
    }

    final Set<RegisteredCommand> commands = new HashSet<>();

    public SpigotColonel(JavaPlugin plugin) {
        this.plugin = plugin;
        this.audiences = BukkitAudiences.create(plugin);

        try {
            commandMap = (SimpleCommandMap) plugin.getServer().getClass()
                    .getMethod("getCommandMap").invoke(plugin.getServer());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(@NotNull String path, @NotNull CommandHandler handler) {
        super.register(path, handler);

        String firstLiteral = path.split(" ")[0];
        SpigotCommand cmd = commands.stream()
                .filter(c -> (c.path() + " ").startsWith(firstLiteral + " "))
                .map(c -> c.command)
                .findFirst().orElse(null);

        if (cmd == null) {
            cmd = new SpigotCommand(this, firstLiteral);
            commandMap.register(plugin.getName().toLowerCase(), cmd);
        }

        commands.add(new RegisteredCommand(path, handler, cmd));
    }

    @Override
    protected Audience audience(CommandSender source) {
        return audiences.sender(source);
    }

    @Override
    protected void build(@NotNull Method method, @NotNull CommandHandlerBuilder<CommandSender> builder) {
        super.build(method, builder);

        Permission permissionConf = method.getAnnotation(Permission.class);
        if (permissionConf != null) {
            builder.condition(s -> s.hasPermission(permissionConf.value()));
        }
    }

    //

    private void registerDefaultTypes() {
        // PLAYER
        registerParameterType(Player.class,
                (ctx, input) ->
                        plugin.getServer().getOnlinePlayers().stream().map(p -> new Suggestion(p.getName())).toList(),
                (ctx, input) -> {
                    Player player = plugin.getServer().getPlayer(input);
                    if (player != null) {
                        return Argument.success(player);
                    } else {
                        return Argument.fail(() -> ctx.source().sendMessage("Player not found"));
                    }
                });

        registerParameterType(Audience.class,
                (ctx, input) ->
                        plugin.getServer().getOnlinePlayers().stream().map(p -> new Suggestion(p.getName())).toList(),
                (ctx, input) -> {
                    Player player = plugin.getServer().getPlayer(input);
                    if (player != null) {
                        return Argument.success(audiences.player(player));
                    } else {
                        return Argument.fail(() -> ctx.source().sendMessage("Player not found"));
                    }
                });

        // WORLD
        registerParameterType(World.class,
                (ctx, input) ->
                        plugin.getServer().getWorlds().stream().map(w -> new Suggestion(w.getName())).toList(),
                (ctx, input) -> {
                    World world = plugin.getServer().getWorld(input);
                    if (world != null) {
                        return Argument.success(world);
                    } else {
                        return Argument.fail(() -> ctx.source().sendMessage("World not found"));
                    }
                });

        // MATERIAL
        registerParameterType(Material.class,
                (ctx, input) ->
                        Set.of(Material.values()).stream().map(m -> new Suggestion(m.getKey().toString())).toList(),
                (ctx, input) -> {
                    Material material = Material.matchMaterial(input);
                    if (material != null) {
                        return Argument.success(material);
                    } else {
                        return Argument.fail(() -> ctx.source().sendMessage("Material not found"));
                    }
                });

        // SOUND
        registerParameterType(Sound.class,
                (ctx, input) ->
                        Set.of(Sound.values()).stream().map(m -> new Suggestion(m.getKey().toString())).toList(),
                (ctx, input) -> {
                    Sound sound = Arrays.stream(Sound.values()).filter(s -> s.getKey().toString().equals(input))
                            .findFirst().orElse(null);
                    if (sound != null) {
                        return Argument.success(sound);
                    } else {
                        return Argument.fail(() -> ctx.source().sendMessage("Sound not found"));
                    }
                });

        // ENTITY TYPE
        registerParameterType(EntityType.class,
                (ctx, input) ->
                        Set.of(EntityType.values()).stream().map(m -> new Suggestion(m.getKey().toString())).toList(),
                (ctx, input) -> {
                    EntityType entityType = Arrays.stream(EntityType.values()).filter(s -> s.getKey().toString().equals(input))
                            .findFirst().orElse(null);
                    if (entityType != null) {
                        return Argument.success(entityType);
                    } else {
                        return Argument.fail(() -> ctx.source().sendMessage("Entity type not found"));
                    }
                });

        // COMPONENT
        registerParameterType(Component.class,
                (ctx, input) -> List.of(),
                (ctx, input) -> {
                    Component component = MixedLegacyComponentSerializer.deserialize(input);
                    return Argument.success(component);
                });
    }

    private void sendError(CommandSender source, String i18n, String fallback, Object... args) {
        if ( plugin.getServer().getPluginManager().isPluginEnabled("BrickI18n") ) {
            SpigotI18nAPI.get(plugin).send(source, "global:" + i18n, args);
            return;
        }

        String str = fallback;
        for ( int i = 0; i < args.length; i++ ) {
            str = str.replace("{" + i + "}", args[i].toString());
        }
        source.sendMessage(ChatColor.RED + str);
    }

}
