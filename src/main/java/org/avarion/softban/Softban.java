package org.avarion.softban;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class Softban extends JavaPlugin {
    private NamespacedKey softBanLevelKey;
    private SoftbanDb db;

    @Override
    public void onEnable() {
        softBanLevelKey = new NamespacedKey(this, "softBanLevel");
        db = new SoftbanDb(this);

        new SoftbanCommand(this);
        new SoftbanEvents(this);
    }

    public NamespacedKey getKey() {
        return softBanLevelKey;
    }

    public SoftbanDb getDb() {
        return db;
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
