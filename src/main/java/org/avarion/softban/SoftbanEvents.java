package org.avarion.softban;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SoftbanEvents implements Listener {
    private final Random random = new Random();
    private final Map<Player, Long> chatCooldowns = new HashMap<>();

    SoftbanEvents() {
        Softban.plugin.getServer().getPluginManager().registerEvents(this, Softban.plugin);
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Softban.getDb().contains(player.getName())) {
            Softban.plugin.handleOnlinePlayer(player, Softban.getDb().get(player.getName()));
        }
    }

    @EventHandler
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            int level = Utils.getSoftbanLevel(player);
            if (level > 0) {
                event.setDamage(event.getDamage() * (1 + 0.1 * level));
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            int level = Utils.getSoftbanLevel(player);
            if (level > 0) {
                event.setDamage(event.getDamage() * (1 - 0.1 * level));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        int level = Utils.getSoftbanLevel(player);
        if (level > 0) {
            Vector velocity = player.getVelocity();
            velocity.multiply(1 - 0.1 * level);
            player.setVelocity(velocity);
/*
            if (random.nextDouble() < 0.001 * level) {
                int radius = 100 * level;
                int x = player.getLocation().getBlockX() + random.nextInt(radius * 2) - radius;
                int z = player.getLocation().getBlockZ() + random.nextInt(radius * 2) - radius;
                int y = player.getWorld().getHighestBlockYAt(x, z);
                player.teleport(new Location(player.getWorld(), x, y, z));
                player.sendMessage("You've been randomly teleported!");
            }
 */
        }
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();
        int level = Utils.getSoftbanLevel(player);
        if (level > 0 && random.nextDouble() < 0.1 * level) {
            event.setDropItems(false);
        }
    }

    @EventHandler
    public void onPlayerExpChange(@NotNull PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        int level = Utils.getSoftbanLevel(player);
        if (level > 0) {
            int newAmount = (int) (event.getAmount() * (1 - 0.1 * level));
            event.setAmount(Math.max(newAmount, 1));
        }
    }

    @EventHandler
    public void onFoodLevelChange(@NotNull FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            int level = Utils.getSoftbanLevel(player);
            if (level > 0 && event.getFoodLevel() < player.getFoodLevel()) {
                event.setFoodLevel(event.getFoodLevel() - level);
            }
        }
    }

    @EventHandler
    public void onPlayerItemDamage(@NotNull PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        int level = Utils.getSoftbanLevel(player);
        if (level > 0) {
            event.setDamage(event.getDamage() + level);
        }
    }

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player) {
            int level = Utils.getSoftbanLevel(player);
            if (level > 0) {
                int slotsToBlock = level * 2;
                for (int i = 0; i < slotsToBlock && i < player.getInventory().getSize(); i++) {
                    player.getInventory().setItem(i, new ItemStack(Material.BARRIER));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(@NotNull PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        int level = Utils.getSoftbanLevel(player);
        if (level > 0 && event.isSneaking()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100 * level, level - 1));
        }
    }
}