package org.avarion.softban;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoftbanDb {
    private final Map<String, Integer> softBanLevels = new HashMap<>();
    private final Softban plugin;
    private final File fileName;

    SoftbanDb(@NotNull Softban plugin) {
        this.plugin = plugin;
        this.fileName = new File(plugin.getDataFolder(), "offline_users.yml");

        load_previous();
    }

    private void load_previous() {
        if (!fileName.exists()) {
            return;
        }

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(fileName);
        final List<Map<?, ?>> usersList = config.getMapList("users");

        for (Map<?, ?> userMap : usersList) {
            for (Map.Entry<?, ?> entry : userMap.entrySet()) {
                try {
                    final String username = entry.getKey().toString().trim();
                    final int level = Integer.parseInt(entry.getValue().toString().trim());

                    if (level < 1 || level > 5) {
                        plugin.getLogger().warning("Invalid level for user: " + username);
                    }
                    else {
                        softBanLevels.put(username, level);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().severe("Invalid data format for user: " + entry.getKey() + " with value: " + entry.getValue());
                }
            }
        }
    }

    private void save() {
        final YamlConfiguration config = new YamlConfiguration();

        List<Map<?, ?>> users = config.getMapList("users");
        for (Map.Entry<String, Integer> entry : softBanLevels.entrySet()) {
            final Map<String, Integer> userMap = new HashMap<>();
            userMap.put(entry.getKey(), entry.getValue());
            users.add(userMap);
        }

        try {
            config.save(fileName);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save offline users.yml: " + e.getMessage());
        }
    }

    public void put(@NotNull final String userName, int level) {
        softBanLevels.put(userName, level);
        save();
    }

    public void remove(@NotNull final String userName) {
        softBanLevels.remove(userName);
    }

    public boolean contains(@NotNull final String userName) {
        return softBanLevels.containsKey(userName);
    }

    public @Nullable Integer get(@NotNull final String userName) {
        return softBanLevels.get(userName);
    }
}
