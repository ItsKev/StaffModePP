package io.github.itskev.staffmodepp.inventory;

import org.bukkit.inventory.ItemStack;

public class Module {

    private String moduleName;
    private ItemStack itemStack;

    public Module(String moduleName, ItemStack itemStack) {
        this.moduleName = moduleName;
        this.itemStack = itemStack;
    }

    public String getModuleName() {
        return moduleName;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
