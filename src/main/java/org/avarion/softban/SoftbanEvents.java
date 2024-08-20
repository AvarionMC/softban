package org.avarion.softban;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class SoftbanEvents implements Listener {
    private final Softban plugin;

    SoftbanEvents(@NotNull Softban plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            PersistentDataContainer container = player.getPersistentDataContainer();
            Integer level = container.get(plugin.getKey(), PersistentDataType.INTEGER);
            if (level != null && level > 0) {
                event.setDamage(event.getDamage() * (1 + 0.1 * level));
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            PersistentDataContainer container = player.getPersistentDataContainer();
            Integer level = container.get(plugin.getKey(), PersistentDataType.INTEGER);
            if (level != null && level > 0) {
                event.setDamage(event.getDamage() * (1 - 0.1 * level));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();

        PersistentDataContainer container = player.getPersistentDataContainer();
        Integer level = container.get(plugin.getKey(), PersistentDataType.INTEGER);

        if (level != null && level > 0) {
            Vector velocity = event.getPlayer().getVelocity();
            velocity.multiply(1 - 0.1 * level);
            event.getPlayer().setVelocity(velocity);
        }
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (plugin.getDb().contains(player.getName())) {
            plugin.handleOnlinePlayer(player, plugin.getDb().get(player.getName()));
        }
    }
}
