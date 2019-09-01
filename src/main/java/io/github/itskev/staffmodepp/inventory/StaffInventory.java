package io.github.itskev.staffmodepp.inventory;

import io.github.itskev.staffmodepp.manager.DataHandler;
import io.github.itskev.staffmodepp.util.ItemHelper;
import io.github.itskev.staffmodepp.util.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class StaffInventory {

    private Map<UUID, List<ItemStack>> savedInventories;
    private Map<Integer, Module> staffInventory;
    private HotBarEventHandler hotBarEventHandler;

    public StaffInventory(Plugin plugin, DataHandler dataHandler) {
        savedInventories = new HashMap<>();
        staffInventory = new HashMap<>();
        hotBarEventHandler = HotBarEventHandler.getInstance(plugin, dataHandler);

        //TODO: get from config
        staffInventory.put(0, new Module("Vanish", ItemHelper.createItem(
                new ItemStack(XMaterial.POTION.parseItem()),
                ChatColor.GOLD + "Toggle Vanish")));
        staffInventory.put(1, new Module("NoClip", ItemHelper.createItem(
                new ItemStack(XMaterial.FEATHER.parseItem()),
                ChatColor.GOLD + "Toggle NoClip")));

        hotBarEventHandler.setModules(staffInventory.values());
    }

    public void saveInventory(Player player) {
        List<ItemStack> itemsInHotbar = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < 9; i++) {
            itemsInHotbar.add(inventory.getItem(i));
            Module module = staffInventory.get(i);
            if (module != null) {
                inventory.setItem(i, module.getItemStack().clone());
            } else {
                inventory.setItem(i, null);
            }
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
