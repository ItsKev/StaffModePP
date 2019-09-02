package io.github.itskev.staffmodepp.inventory.gui;

import org.bukkit.inventory.ItemStack;

public class GUIItem {

    private ItemStack itemStack;
    private String command;

    public GUIItem(ItemStack itemStack, String command) {
        this.itemStack = itemStack;
        this.command = command;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getCommand() {
        return command;
    }
}
