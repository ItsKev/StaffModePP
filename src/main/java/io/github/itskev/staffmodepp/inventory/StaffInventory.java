package io.github.itskev.staffmodepp.inventory;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import io.github.itskev.staffmodepp.util.ItemHelper;
import io.github.itskev.staffmodepp.util.XMaterial;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class StaffInventory {

    private Map<UUID, List<ItemStack>> savedInventories;
    private Map<Integer, Module> staffInventory;

    public StaffInventory(Plugin plugin, DataHandler dataHandler) {
        savedInventories = new HashMap<>();
        staffInventory = new HashMap<>();

        FileConfiguration config = plugin.getConfig();
        List<String> playerOptionsGUI = new ArrayList<>(config.getConfigurationSection("HotBar").getKeys(false));
        for (String entry : playerOptionsGUI) {
            String[] split = config.getString("HotBar." + entry + ".Type").split(":");
            ItemStack itemStack;
            if (split.length > 1) {
                itemStack = new ItemStack(Integer.parseInt(split[0]), 1, Short.parseShort(split[1]));
            } else {
                itemStack = new ItemStack(Integer.parseInt(split[0]));
            }
            ItemStack item = ItemHelper.createItem(itemStack, ConfigHelper.getStringFromConfig("HotBar." + entry + ".Name"),
                    ConfigHelper.getStringListFromConfig("HotBar." + entry + ".Lore", null).toArray(new String[0]));
            String moduleName = ConfigHelper.getStringFromConfig("HotBar." + entry + ".Module");
            staffInventory.put(Integer.parseInt(entry), new Module(moduleName, item));
        }
        HotBarEventHandler.getInstance(plugin, dataHandler).setModules(staffInventory.values());
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
                    ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
                    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                    ItemStack itemStack = module.getItemStack();
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    skullMeta.setDisplayName(itemMeta.getDisplayName());
                    skullMeta.setLore(itemMeta.getLore());
                    skullMeta.setOwner(player.getName());
                    skull.setItemMeta(skullMeta);
                    inventory.setItem(i, skull);
                } else if (module.getModuleName().equals("No Clip")) {
                    ItemStack itemStack = module.getItemStack().clone();
                    ItemMeta itemMeta = module.getItemStack().clone().getItemMeta();
                    if (itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("ON", "OFF"));
                    }
                    itemStack.setItemMeta(itemMeta);
                    inventory.setItem(i, itemStack);
                } else {
                    inventory.setItem(i, module.getItemStack().clone());
                }
            } else {
                inventory.setItem(i, null);
            }
        }
        savedInventories.put(player.getUniqueId(), itemsInHotbar);
        player.updateInventory();
    }

    public void setOn(Player player, String moduleName) {
        staffInventory.entrySet().stream()
                .filter(integerModuleEntry -> integerModuleEntry.getValue().getModuleName().equals(moduleName))
                .findFirst()
                .ifPresent(integerModuleEntry -> {
                    ItemStack item = player.getInventory().getItem(integerModuleEntry.getKey());
                    ItemMeta itemMeta = item.getItemMeta();
                    if (itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("OFF", "ON"));
                    }
                    item.setItemMeta(itemMeta);
                });
    }

    public void setOff(Player player, String moduleName) {
        staffInventory.entrySet().stream()
                .filter(integerModuleEntry -> integerModuleEntry.getValue().getModuleName().equals(moduleName))
                .findFirst()
                .ifPresent(integerModuleEntry -> {
                    ItemStack item = player.getInventory().getItem(integerModuleEntry.getKey());
                    ItemMeta itemMeta = item.getItemMeta();
                    if (itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("ON", "OFF"));
                    }
                    item.setItemMeta(itemMeta);
                });
    }

    public void restoreInventory(Player player) {
        List<ItemStack> hotbar = savedInventories.remove(player.getUniqueId());
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, hotbar.get(i));
        }
    }
}
