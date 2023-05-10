package com.guflimc.colonel.minecraft.spigot;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.colonel.common.safe.SafeCommandContext;
import com.guflimc.colonel.common.safe.SafeCommandHandlerBuilder;
import com.guflimc.colonel.common.dispatch.tree.CommandHandler;
import com.guflimc.colonel.minecraft.common.MinecraftColonel;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class SpigotColonel extends MinecraftColonel<CommandSender> {

    final JavaPlugin plugin;
    final BukkitAudiences audiences;

    private final SimpleCommandMap commandMap;

    record RegisteredCommand(@NotNull String path, @NotNull CommandHandler handler, @NotNull SpigotCommand command) {
    }

    final Set<RegisteredCommand> commands = new HashSet<>();

    public SpigotColonel(JavaPlugin plugin) {
        super(CommandSender.class);

        this.plugin = plugin;
        this.audiences = BukkitAudiences.create(plugin);

        try {
            commandMap = (SimpleCommandMap) plugin.getServer().getClass()
                    .getMethod("getCommandMap").invoke(plugin.getServer());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        registerAll(new SpigotArguments(this));
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
    protected void build(@NotNull Method method, @NotNull Map<Parameter, Function<SafeCommandContext<CommandSender>, Object>> suppliers, @NotNull SafeCommandHandlerBuilder<CommandSender> builder) {
        super.build(method, suppliers, builder);

        Permission permissionConf = method.getAnnotation(Permission.class);
        if (permissionConf != null) {
            builder.condition(s -> s.hasPermission(permissionConf.value()));
        }
    }

    //

    void sendMessage(CommandSender source, String i18n, String fallback, Object... args) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("BrickI18n")) {
            SpigotI18nAPI.get(plugin).send(source, i18n, args);
            return;
        }

        String str = fallback;
        for (int i = 0; i < args.length; i++) {
            str = str.replace("{" + i + "}", args[i].toString());
        }
        source.sendMessage(str);
    }
}
