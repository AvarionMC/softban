package org.avarion.softban;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SoftbanCommand implements TabExecutor {
    private final Map<String, Long> confirmationMap = new HashMap<>();

    SoftbanCommand() {
        registerMyself();
    }

    private void registerMyself() {
        PluginCommand cmd = Softban.plugin.getCommand("softban");
        if (cmd==null) {
            throw new RuntimeException("softban command not found");
        }

        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isEnabled(CommandSender player) {
        return Softban.plugin.isEnabled() && (player.isOp() || player.hasPermission("softban.admin"));
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

        if (args.length < 2) {
            return false;
        }

        String subCommand = args[0].toLowerCase();
        String username = args[1];

        return switch (subCommand) {
            case "set" -> {
                if (args.length!=3) {
                    yield false;
                }
                yield handleSetCommand(sender, username, args[2]);
            }
            case "remove" -> handleRemoveCommand(sender, username);
            default -> false;
        };
    }

    private boolean handleSetCommand(CommandSender sender, String username, String sLevel) {
        int level;
        try {
            level = parseLevel(sLevel);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Invalid level. Use a number between 1 and 5.");
            return false;
        }

        Player onlinePlayer = Bukkit.getPlayer(username);
        if (onlinePlayer!=null) {
            return Softban.plugin.handleOnlinePlayer(onlinePlayer, level);
        }

        handleOfflinePlayerSet(sender, username, level);
        return true;
    }

    private void handleOfflinePlayerSet(@NotNull CommandSender sender, String username, int level) {
        String confirmationKey = sender.getName() + ":" + username + ":set:" + level;

        if (confirmationMap.containsKey(confirmationKey) && System.currentTimeMillis() - confirmationMap.get(confirmationKey) < 15000) {
            confirmationMap.remove(confirmationKey);
            if (Softban.plugin.handleOfflinePlayer(username, level)) {
                sender.sendMessage("Soft ban level set to " + level + " for offline player " + username);
            }
        }
        else {
            confirmationMap.put(confirmationKey, System.currentTimeMillis());
            sendConfirmationMessage(sender, "/softban set " + username + " " + level,
                    "Confirm setting soft ban level " + level + " for offline player " + username
            );
        }
    }

    private boolean handleRemoveCommand(CommandSender sender, String username) {
        Player onlinePlayer = Bukkit.getPlayer(username);
        if (onlinePlayer!=null) {
            return Softban.plugin.handleOnlinePlayer(onlinePlayer, 0);
        }

        handleOfflinePlayerRemove(sender, username);
        return true;
    }

    private void handleOfflinePlayerRemove(@NotNull CommandSender sender, String username) {
        String confirmationKey = sender.getName() + ":" + username + ":remove";
        if (confirmationMap.containsKey(confirmationKey) && System.currentTimeMillis() - confirmationMap.get(confirmationKey) < 15000) {
            confirmationMap.remove(confirmationKey);
            if (Softban.plugin.handleOfflinePlayer(username, 0)) {
                sender.sendMessage("Soft ban removed for offline player " + username);
            }
        }
        else {
            confirmationMap.put(confirmationKey, System.currentTimeMillis());
            sendConfirmationMessage(sender, "/softban remove " + username, "Confirm removing soft ban for offline player " + username);
        }
    }

    @SuppressWarnings("deprecation")
    private void sendConfirmationMessage(CommandSender sender, String command, String message) {
        if (sender instanceof Player player) {
            TextComponent component = new TextComponent(message + " (Click to confirm)");
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            player.spigot().sendMessage(component);
        }
        else {
            sender.sendMessage(message + " (Run the command again within 15 seconds to confirm)");
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        if (!isEnabled(sender)) {
            return null;
        }

        List<String> completions = new ArrayList<>();
        if (args.length==1) {
            completions.addAll(Arrays.asList("set", "remove"));
        }
        else if (args.length==2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        else if (args.length==3 && args[0].equalsIgnoreCase("set")) {
            for (int i = 1; i <= 5; i++) {
                completions.add(String.valueOf(i));
            }
        }
        return completions;
    }
}