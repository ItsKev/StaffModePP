package io.github.itskev.staffmodepp.inventory;

import io.github.itskev.staffmodepp.manager.DataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;

public class HotBarEventHandler implements Listener {

    private static HotBarEventHandler hotBarEventHandler;

    public static HotBarEventHandler getInstance(Plugin plugin, DataHandler dataHandler) {
        if (hotBarEventHandler == null) {
            hotBarEventHandler = new HotBarEventHandler(plugin, dataHandler);
        }
        return hotBarEventHandler;
    }

    private DataHandler dataHandler;
    private Collection<ItemStack> modules;

    private HotBarEventHandler(Plugin plugin, DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        modules = new ArrayList<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void setModules(Collection<ItemStack> modules) {
        this.modules = modules;
    }

    @EventHandler
    public void onInventoryClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!dataHandler.isInStaffMode(player)) return;
        ItemStack item = event.getItem();
        if (item != null) {
            if (modules.contains(item)) {
                player.sendMessage("Found");
            }
        }
    }
}
