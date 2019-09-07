package io.github.itskev.staffmodepp.inventory;

import org.bukkit.inventory.ItemStack;

public class Module {

    private String moduleName;
    private ItemStack itemStack;
    private String nameOn;
    private String nameOff;

    public Module(String moduleName, ItemStack itemStack, String nameOn, String nameOff) {
        this.moduleName = moduleName;
        this.itemStack = itemStack;
        this.nameOn = nameOn;
        this.nameOff = nameOff;
    }

    public String getModuleName() {
        return moduleName;
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    public String getNameOn() {
        return nameOn;
    }

    public String getNameOff() {
        return nameOff;
    }
}
