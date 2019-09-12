package io.github.itskev.staffmodepp.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryContent {

    private ItemStack[] inventory;
    private ItemStack[] armor;

    public InventoryContent(ItemStack[] inventory, ItemStack[] armor) {
        this.inventory = inventory;
        this.armor = armor;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public ItemStack[] getArmor() {
        return armor;
    }
}
