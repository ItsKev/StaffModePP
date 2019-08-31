package io.github.itskev.staffmodepp.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemHelper {

    public static ItemStack createItem(Material material, String displayName, String... lore) {
        return createItem(new ItemStack(material), displayName, lore);
    }

    public static ItemStack createItem(ItemStack itemStack, String displayName, String... lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(displayName);
            itemMeta.setLore(Arrays.asList(lore));
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
