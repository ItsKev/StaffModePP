package io.github.itskev.staffmodepp.inventory;

import io.github.itskev.staffmodepp.manager.DataHandler;
import io.github.itskev.staffmodepp.util.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class StaffInventory {

    private Map<UUID, List<ItemStack>> savedInventories;
    private Map<Integer, ItemStack> staffInventory;
    private HotBarEventHandler hotBarEventHandler;

    public StaffInventory(Plugin plugin, DataHandler dataHandler) {
        savedInventories = new HashMap<>();
        staffInventory = new HashMap<>();
        hotBarEventHandler = HotBarEventHandler.getInstance(plugin, dataHandler);

        //TODO: get from config
        staffInventory.put(0, new ItemStack(XMaterial.POTION.parseItem()));
        staffInventory.put(1, new ItemStack(XMaterial.FEATHER.parseItem()));

        hotBarEventHandler.setModules(staffInventory.values());
    }

    public void saveInventory(Player player) {
        List<ItemStack> itemsInHotbar = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < 9; i++) {
            itemsInHotbar.add(inventory.getItem(i));
            inventory.setItem(i, staffInventory.get(i));
        }
        savedInventories.put(player.getUniqueId(), itemsInHotbar);
    }

    public void restoreInventory(Player player) {
        List<ItemStack> hotbar = savedInventories.remove(player.getUniqueId());
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, hotbar.get(i));
        }
    }
}
