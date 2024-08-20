package org.avarion.softban;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class Softban extends JavaPlugin {
    private NamespacedKey softBanLevelKey;
    private SoftbanDb db;
    public static Softban plugin;

    @Override
    public void onEnable() {
        plugin = this;

        softBanLevelKey = new NamespacedKey(this, "softBanLevel");
        db = new SoftbanDb();

        new SoftbanCommand();
        new SoftbanEvents();
    }

    static NamespacedKey getKey() {
        return plugin.softBanLevelKey;
    }

    static SoftbanDb getDb() {
        return plugin.db;
    }

    public boolean handleOnlinePlayer(Player player, Integer level) {
        if (player==null || level==null) {
            return false;
        }

        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(softBanLevelKey, PersistentDataType.INTEGER, level);

        db.remove(player.getName());

        return true;
    }

    public boolean handleOfflinePlayer(String username, int level) {
        db.put(username, level);
        return true;
    }
}
