package com.guflimc.colonel.minecraft.spigot;

import com.guflimc.colonel.common.build.CommandHandlerBuilder;
import com.guflimc.colonel.common.tree.CommandHandler;
import com.guflimc.colonel.minecraft.common.MinecraftColonel;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class SpigotColonel extends MinecraftColonel<CommandSender> {

    private final JavaPlugin plugin;
    private final BukkitAudiences audiences;

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

        registerAll(new SpigotArguments(plugin, audiences));
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

}
