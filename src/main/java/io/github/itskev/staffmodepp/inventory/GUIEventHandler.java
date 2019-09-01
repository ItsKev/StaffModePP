package io.github.itskev.staffmodepp.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIEventHandler implements Listener {

    private static GUIEventHandler guiEventHandler;

    static GUIEventHandler getInstance(Plugin plugin) {
        if (guiEventHandler == null) {
            guiEventHandler = new GUIEventHandler(plugin);
        }
        return guiEventHandler;
    }

    private final Map<UUID, GUI> openGUIs;


    private GUIEventHandler(Plugin plugin) {
        openGUIs = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    void addListener(Player player, GUI gui) {
        openGUIs.putIfAbsent(player.getUniqueId(), gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        openGUIs.values().stream()
                .filter(gui -> event.getView().getTopInventory().equals(gui.getInventory()))
                .findFirst()
                .ifPresent(gui -> {
                    if (event.isShiftClick()) event.setCancelled(true);
                });

        openGUIs.values().stream()
                .filter(gui -> event.getClickedInventory() != null && event.getClickedInventory().equals(gui.getInventory()))
                .findAny().ifPresent(gui -> {
                    ItemStack currentItem = event.getCurrentItem();
                    if (gui.getClickables().containsKey(currentItem)) {
                        gui.getClickables().get(currentItem).onClick();
                    }
                    event.setCancelled(true);
                }
        );
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        openGUIs.values().stream()
                .filter(gui -> event.getView().getTopInventory().equals(gui.getInventory()) &&
                        event.getInventorySlots().stream().anyMatch(slot -> slot < gui.getInventory().getSize()))
                .findAny().ifPresent(gui -> event.setCancelled(true));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        openGUIs.entrySet().removeIf(entry -> event.getInventory().equals(entry.getValue().getInventory()));
    }
}
