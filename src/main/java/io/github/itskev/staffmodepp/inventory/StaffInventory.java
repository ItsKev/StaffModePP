package io.github.itskev.staffmodepp.inventory;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.util.ItemHelper;
import io.github.itskev.staffmodepp.util.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
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
        staffInventory.put(0, new Module("Vanish Mode", ItemHelper.createItem(
                new ItemStack(XMaterial.POTION.parseItem()),
                ChatColor.GOLD + "Toggle Vanish")));
        staffInventory.put(1, new Module("No Clip", ItemHelper.createItem(
                new ItemStack(XMaterial.FEATHER.parseItem()),
                ChatColor.GOLD + "Toggle NoClip")));
        staffInventory.put(2, new Module("Player Options", ItemHelper.createItem(
                XMaterial.PLAYER_HEAD.parseItem(),
                ChatColor.GOLD + "Follow Player")));

        hotBarEventHandler.setModules(staffInventory.values());
    }

    public Map<Integer, Module> getStaffInventory() {
        return staffInventory;
    }

    public void saveInventory(Player player) {
        List<ItemStack> itemsInHotbar = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < 9; i++) {
            itemsInHotbar.add(inventory.getItem(i));
            Module module = staffInventory.get(i);
            if (module != null) {
                if (module.getModuleName().equals("Player Options")) {
                    ItemStack skull = module.getItemStack().clone();
                    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                    skullMeta.setOwner(player.getName());
                    skull.setItemMeta(skullMeta);
                    inventory.setItem(i, skull);
                } else {
                    inventory.setItem(i, module.getItemStack().clone());
                }
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
