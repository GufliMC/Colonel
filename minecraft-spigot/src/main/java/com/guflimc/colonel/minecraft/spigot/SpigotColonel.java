package com.guflimc.colonel.minecraft.spigot;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.dispatch.tree.CommandHandler;
import com.guflimc.colonel.common.exception.CommandFailure;
import com.guflimc.colonel.common.exception.CommandNotFoundFailure;
import com.guflimc.colonel.common.exception.CommandPrepareParameterFailure;
import com.guflimc.colonel.common.safe.SafeCommandContext;
import com.guflimc.colonel.common.safe.SafeCommandHandlerBuilder;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SpigotColonel extends MinecraftColonel<CommandSender> {

    final JavaPlugin plugin;
    final BukkitAudiences audiences;

    private final SimpleCommandMap commandMap;
    private BiConsumer<CommandSender, CommandFailure> errorHandler;

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
            builder.condition(s -> s.hasPermission(replacePlaceholders(permissionConf.value())));
        }
    }

    @Override
    public void dispatch(CommandSender source, String input) {
        try {
            super.dispatch(source, input);
        } catch (CommandFailure failure) {
            handle(source, failure);
        }
    }

    @Override
    public List<Suggestion> suggestions(CommandSender source, String input, int cursor) {
        try {
            return super.suggestions(source, input, cursor);
        } catch (CommandFailure failure) {
            handle(source, failure);
        }
        return List.of();
    }

    //

    public void setErrorHandler(BiConsumer<CommandSender, CommandFailure> errorHandler) {
        this.errorHandler = errorHandler;
    }

    //

    private void handle(CommandSender source, CommandFailure failure) {
        if (errorHandler != null) {
            errorHandler.accept(source, failure);
            return;
        }

        // USER FACING ERRORS

        if (failure instanceof CommandNotFoundFailure) {
            sendMessage(source, "cmd.error.notfound",
                    ChatColor.RED + "Command not found: " + ChatColor.DARK_RED + "{0}", failure.command());
            return;
        }

        if (failure instanceof CommandPrepareParameterFailure pf) {
            if (pf.input() == null) {
                sendMessage(source, "cmd.error.parameter.missing",
                        ChatColor.RED + "The parameter " + ChatColor.DARK_RED + "{0}" +
                                ChatColor.RED + " is missing. Expected syntax: " + ChatColor.DARK_RED + "{1}" +
                                ChatColor.RED + ".", pf.parameter().name(), pf.path() + " " + pf.definition().toString());
                return;
            }
            if (pf.getCause() instanceof IllegalArgumentException) {
                sendMessage(source, "cmd.error.parameter",
                        ChatColor.RED + "The value " + ChatColor.DARK_RED + "{0}" +
                                ChatColor.RED + " is invalid for parameter " + ChatColor.DARK_RED + "{1}" +
                                ChatColor.RED + ".", pf.input(), pf.parameter().name());
                return;
            }
        }

        // INTERNAL ERRORS FOR THE DEVELOPER

        sendMessage(source, "cmd.error.unexpected", ChatColor.RED + "An unexpected error occured, check the console for more information.");

        if (failure.getCause() != null) {
            failure.getCause().printStackTrace();
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
