package org.avarion.softban;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

class Utils {
    static int getSoftbanLevel(@NotNull Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        Integer level = container.get(Softban.getKey(), PersistentDataType.INTEGER);
        return level!=null ? level:0;
    }

}
