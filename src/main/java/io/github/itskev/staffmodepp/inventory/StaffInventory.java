package io.github.itskev.staffmodepp.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class StaffInventory {

    private Map<UUID, List<ItemStack>> savedInventories;
    private Map<Integer, ItemStack> staffInventory;

    public StaffInventory() {
        savedInventories = new HashMap<>();
        staffInventory = new HashMap<>();

        //TODO: get from config
        staffInventory.put(4, new ItemStack(Material.GOLDEN_APPLE));
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
