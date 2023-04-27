package com.guflimc.colonel.minecraft.spigot;

import com.guflimc.colonel.common.suggestion.Suggestion;
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
        colonel.dispatch(sender, input);
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        String input = (getName() + " " + String.join(" ", args));
        return colonel.suggestions(sender, input).stream().map(Suggestion::value).toList();
    }
}
