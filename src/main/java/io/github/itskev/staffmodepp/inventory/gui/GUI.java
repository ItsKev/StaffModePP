package io.github.itskev.staffmodepp.inventory.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface GUI {

    Inventory getInventory();

    void fillBorderWith(ItemStack item);

    void openInventory(Player player);

    void addClickable(ItemStack itemStack, int slot, GUICallback callback);

    void removeClickable(int slot);

    Map<ItemStack, GUICallback> getClickables();

}
