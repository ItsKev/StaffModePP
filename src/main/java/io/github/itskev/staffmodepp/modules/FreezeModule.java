package io.github.itskev.staffmodepp.modules;

import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class FreezeModule implements Listener {

    private Plugin plugin;
    private Set<UUID> frozenPlayers;
    private Map<UUID, Entity> holograms;
    private Map<UUID, BukkitTask> messageTasks;

    public FreezeModule(Plugin plugin) {
        this.plugin = plugin;
        frozenPlayers = new HashSet<>();
        holograms = new HashMap<>();
        messageTasks = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean isFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }

    public void freezePlayer(Player player, Player playerToBeFrozen) {
        player.sendMessage(ConfigHelper.getStringFromConfig("Freeze.Freeze-Message", playerToBeFrozen.getDisplayName()));
        if (!isFrozen(playerToBeFrozen)) {
            frozenPlayers.add(playerToBeFrozen.getUniqueId());
            createHologram(playerToBeFrozen, ConfigHelper.getStringFromConfig("Freeze.Hologram", playerToBeFrozen.getDisplayName()));
            messageTasks.put(playerToBeFrozen.getUniqueId(), plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                playerToBeFrozen.sendMessage(ConfigHelper.getStringFromConfig("Freeze.MessageSentToPlayer"));
            }, 0, 5 * 20));
        }
    }

    public void unfreezePlayer(Player player, Player playerToBeUnFrozen) {
        if (player != null) {
            player.sendMessage(ConfigHelper.getStringFromConfig("Freeze.Unfreeze-Message", playerToBeUnFrozen.getDisplayName()));
        }
        if (isFrozen(playerToBeUnFrozen)) {
            frozenPlayers.remove(playerToBeUnFrozen.getUniqueId());
            Entity entity = holograms.remove(playerToBeUnFrozen.getUniqueId());
            entity.remove();
            BukkitTask bukkitTask = messageTasks.remove(playerToBeUnFrozen.getUniqueId());
            bukkitTask.cancel();
        }
    }

    public void refreezePlayer(Player playerToBeFrozen) {
        frozenPlayers.add(playerToBeFrozen.getUniqueId());
        createHologram(playerToBeFrozen, ConfigHelper.getStringFromConfig("Freeze.Hologram", playerToBeFrozen.getDisplayName()));
        messageTasks.put(playerToBeFrozen.getUniqueId(), plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            playerToBeFrozen.sendMessage(ConfigHelper.getStringFromConfig("Freeze.MessageSentToPlayer"));
        }, 0, 5 * 20));
    }

    public void unfreezePlayerTemp(Player playerToBeUnFrozen) {
        if (isFrozen(playerToBeUnFrozen)) {
            Entity entity = holograms.remove(playerToBeUnFrozen.getUniqueId());
            entity.remove();
            BukkitTask bukkitTask = messageTasks.remove(playerToBeUnFrozen.getUniqueId());
            bukkitTask.cancel();
        }
    }

    private void createHologram(Player player, String message) {
        Location spawnLocation = player.getLocation().clone();
        spawnLocation.setY(spawnLocation.getY() + 0.5);
        ArmorStand armorStand = (ArmorStand) player.getLocation().getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomName(message);
        armorStand.setCustomNameVisible(true);
        holograms.put(player.getUniqueId(), armorStand);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // TODO: bug when player on top
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent event) {
        if (frozenPlayers.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        if (frozenPlayers.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        if (!e.getRightClicked().isVisible()) {
            e.setCancelled(true);
        }
    }
}
