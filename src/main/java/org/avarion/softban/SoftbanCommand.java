package org.avarion.softban;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SoftbanCommand implements TabExecutor {
    private final Softban plugin;

    SoftbanCommand(@NotNull Softban plugin) {
        this.plugin = plugin;

        registerMyself(plugin);
    }

    private void registerMyself(@NotNull Softban plugin) {
        PluginCommand cmd = plugin.getCommand("softban");
        if (cmd==null) {
            throw new RuntimeException("softban command not found");
        }

        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isEnabled(CommandSender player) {
        return plugin.isEnabled() && (player.isOp() || player.hasPermission("softban.admin"));
    }

    private int parseLevel(final String sLevel) throws IllegalArgumentException {
        int level = Integer.parseInt(sLevel);
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Level must be between 1 and 5");
        }
        return level;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!isEnabled(sender)) {
            return false;
        }

        if (args.length!=2) {
            return false;
        }

        int level;
        try {
            level = parseLevel(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Invalid level. Use a number between 1 and 5.");
            return false;
        }

        if (plugin.handleOnlinePlayer(Bukkit.getPlayer(args[0]), level) || plugin.handleOfflinePlayer(args[0], level) ) {
            sender.sendMessage("Soft ban level set to " + level + " for player " + args[0]);
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        if (!isEnabled(sender)) {
            return null;
        }

        List<String> completions = new ArrayList<>();
        if (args.length==1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }

        }
        else if (args.length==2) {
            for (int i = 1; i <= 5; i++) {
                completions.add(String.valueOf(i));
            }
        }
        return completions;
    }
}
