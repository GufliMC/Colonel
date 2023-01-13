package com.guflimc.colonel.spigot.api;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColonelCommand extends org.bukkit.command.Command {

    private final SpigotColonelManager manager;

    public ColonelCommand(@NotNull SpigotColonelManager manager, @NotNull String firstLiteral) {
        super(firstLiteral);
        this.manager = manager;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        try {
            manager.colonel.dispatcher().execute(commandLabel + " " + String.join(" ", args), sender);
        } catch (CommandSyntaxException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return false;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        ParseResults<CommandSender> result = manager.colonel.dispatcher().parse(alias + " " + String.join(" ", args), sender);
        Suggestions suggestions = manager.colonel.dispatcher().getCompletionSuggestions(result).join();
        return suggestions.getList().stream().map(Suggestion::getText).toList();
    }
}
