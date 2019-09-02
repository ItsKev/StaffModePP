package io.github.itskev.staffmodepp.inventory.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIImpl implements GUI {

    private Inventory inventory;
    private Plugin plugin;
    private Map<ItemStack, GUICallback> clickables;

    GUIImpl(Inventory inventory, Plugin plugin) {
        this.inventory = inventory;
        this.plugin = plugin;
        clickables = new HashMap<>();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void fillBorderWith(ItemStack item) {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            slots.add(i);
        }

        int rows = inventory.getSize() / 9;
        for (int i = 1; i < rows; i++) {
            slots.add(i * 9);
            slots.add(i * 9 + 8);
        }
        for (int i = inventory.getSize() - 10; i < inventory.getSize(); i++) {
            slots.add(i);
        }

        slots.forEach(slot -> inventory.setItem(slot, item));
    }

    @Override
    public void openInventory(Player player) {
        player.openInventory(inventory);
        GUIEventHandler.getInstance(plugin).addListener(player, this);
    }

    @Override
    public void addClickable(ItemStack itemStack, int slot, GUICallback callback) {
        inventory.setItem(slot, itemStack);
        clickables.putIfAbsent(itemStack, callback);
    }

    @Override
    public void removeClickable(int slot) {
        ItemStack item = inventory.getItem(slot);
        inventory.setItem(slot, null);
        clickables.remove(item);
    }

    @Override
    public Map<ItemStack, GUICallback> getClickables() {
        return clickables;
    }
}
