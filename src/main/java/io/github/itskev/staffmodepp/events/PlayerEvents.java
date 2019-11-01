package io.github.itskev.staffmodepp.events;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.modules.VanishModule;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import me.neznamy.tab.api.EnumProperty;
import me.neznamy.tab.api.TABAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PlayerEvents implements Listener {

    private Plugin plugin;
    private DataHandler dataHandler;

    private Map<Location, Inventory> openInventoriesInVanish = new HashMap<>();

    public PlayerEvents(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (dataHandler.isInStaffMode(event.getPlayer()) || dataHandler.getVanishModule().isVanished(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (dataHandler.isInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (dataHandler.isInStaffMode(player) &&
                !player.hasPermission(ConfigHelper.getStringFromConfig("Staffmode.Restrictionbypass"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryCreativeClick(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission(ConfigHelper.getStringFromConfig("Staffmode.Restrictionbypass"))) {
            player.closeInventory();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::updateInventory, 1);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (dataHandler.isInStaffMode((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            Server server = plugin.getServer();
            Player player = event.getPlayer();
            String vanishPermission = ConfigHelper.getStringFromConfig("Vanish.Permission-To-View");
            if (!player.hasPermission(vanishPermission)) {
                dataHandler.getVanishModule().getVanishedPlayers().forEach(uuid -> {
                    Player serverPlayer = server.getPlayer(uuid);
                    if (serverPlayer != null) {
                        player.hidePlayer(serverPlayer);
                    }
                });
            }
            if (dataHandler.getVanishModule().isVanished(player)) {
                String prefix = ConfigHelper.getStringFromConfig("Vanish.Prefix-In-Vanish");
                String originalValue = TABAPI.getOriginalValue(player.getUniqueId(), EnumProperty.TAGPREFIX);
                TABAPI.setValueTemporarily(player.getUniqueId(), EnumProperty.TAGPREFIX, originalValue + prefix);
                plugin.getServer().getOnlinePlayers().forEach(o -> {
                    if (!o.hasPermission(vanishPermission)) {
                        o.hidePlayer(player);
                    } else {
                        o.hidePlayer(player);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> o.showPlayer(player), 1);
                    }
                });
            }
            if (dataHandler.getFreezeModule().isFrozen(player)) {
                dataHandler.getFreezeModule().refreezePlayer(player);
            }
        }, 5);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (dataHandler.isInStaffMode(player)) {
            dataHandler.getNoClipModule().removeNoClipPlayer(player);
        }
        if (dataHandler.getFreezeModule().isFrozen(player)) {
            String stringFromConfig = ConfigHelper.getStringFromConfig("Freeze.CommandExecutedOnDisconnect", event.getPlayer().getDisplayName());
            if (!stringFromConfig.equals("")) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                        stringFromConfig);
                dataHandler.getFreezeModule().unfreezePlayer(null, player);
            } else {
                dataHandler.getFreezeModule().unfreezePlayerTemp(player);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.CHEST)) {
            Chest chest = (Chest) event.getClickedBlock().getState();
            if (openInventoriesInVanish.containsKey(chest.getLocation())) {
                Inventory inventory = openInventoriesInVanish.get(chest.getLocation());
                Bukkit.getScheduler().runTaskLater(plugin, () -> event.getPlayer().openInventory(inventory), 1);
            } else if (dataHandler.getVanishModule().isVanished(event.getPlayer())) {
                event.setCancelled(true);
                Inventory inventory = this.plugin.getServer().createInventory(event.getPlayer(), chest.getInventory().getSize());
                inventory.setContents(chest.getInventory().getContents());
                event.getPlayer().openInventory(inventory);
                openInventoriesInVanish.put(chest.getLocation(), inventory);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (dataHandler.getVanishModule().isVanished((Player) event.getPlayer())) {
            Location loc = null;
            for (Map.Entry<Location, Inventory> entry : openInventoriesInVanish.entrySet()) {
                if (entry.getValue().equals(event.getInventory())) {
                    Chest chest = (Chest) entry.getKey().getBlock().getState();
                    chest.getInventory().setContents(event.getInventory().getContents());
                    loc = entry.getKey();
                    break;
                }
            }
            if (loc != null) {
                openInventoriesInVanish.remove(loc);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        VanishModule vanishModule = dataHandler.getVanishModule();
        if (vanishModule.isVanished(player)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                String vanishPermission = ConfigHelper.getStringFromConfig("Vanish.Permission-To-View");
                plugin.getServer().getOnlinePlayers().forEach(p -> {
                    if (!p.hasPermission(vanishPermission)) {
                        p.hidePlayer(player);
                    }
                });
            }, 5);
        }
    }
}
