package com.guflimc.colonel.minecraft.spigot;

import com.guflimc.colonel.common.build.exception.CommandHandleException;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.exception.CommandDispatchException;
import com.guflimc.colonel.common.exception.CommandNotFoundException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpigotCommand extends Command {

    private final SpigotColonel colonel;

    public SpigotCommand(@NotNull SpigotColonel colonel, @NotNull String firstLiteral) {
        super(firstLiteral);
        this.colonel = colonel;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        String input = (getName() + " " + String.join(" ", args));
        try {
            colonel.dispatch(sender, input);
        } catch (CommandNotFoundException e) {
            colonel.sendMessage(sender, "cmd.error.notfound", ChatColor.RED + e.getMessage(), input);
        } catch (CommandDispatchException | CommandHandleException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            if ( e.getCause() != null ) {
                e.getCause().printStackTrace();
            }
        }
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        String input = (getName() + " " + String.join(" ", args));
        return colonel.suggestions(sender, input).stream().map(Suggestion::value).toList();
    }
}
