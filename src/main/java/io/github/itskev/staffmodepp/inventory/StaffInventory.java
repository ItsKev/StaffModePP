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
            String nameOff = ConfigHelper.getStringFromConfig("HotBar." + entry + ".NameOff");
            String nameOn = ConfigHelper.getStringFromConfig("HotBar." + entry + ".NameOn");
            ItemStack item = ItemHelper.createItem(itemStack, nameOff,
                    ConfigHelper.getStringListFromConfig("HotBar." + entry + ".Lore", null).toArray(new String[0]));
            String moduleName = ConfigHelper.getStringFromConfig("HotBar." + entry + ".Module");
            staffInventory.put(Integer.parseInt(entry), new Module(moduleName, item, nameOn, nameOff));
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
        staffInventory.values().stream()
                .filter(module -> module.getModuleName().equals(moduleName))
                .findFirst()
                .ifPresent(module -> {
                    ItemStack itemStack = module.getItemStack();
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setDisplayName(module.getNameOff());
                    itemStack.setItemMeta(meta);
                    Arrays.stream(player.getInventory().getContents())
                            .filter(itemStack::equals)
                            .findFirst()
                            .ifPresent(itemStack1 -> {
                                ItemMeta itemMeta = itemStack1.getItemMeta();
                                itemMeta.setDisplayName(module.getNameOn());
                                itemStack1.setItemMeta(itemMeta);
                            });
                });
    }

    public void setOff(Player player, String moduleName) {
        staffInventory.values().stream()
                .filter(module -> module.getModuleName().equals(moduleName))
                .findFirst()
                .ifPresent(module -> {
                    ItemStack itemStack = module.getItemStack();
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setDisplayName(module.getNameOn());
                    itemStack.setItemMeta(meta);
                    Arrays.stream(player.getInventory().getContents())
                            .filter(itemStack::equals)
                            .findFirst()
                            .ifPresent(itemStack1 -> {
                                ItemMeta itemMeta = itemStack1.getItemMeta();
                                itemMeta.setDisplayName(module.getNameOff());
                                itemStack1.setItemMeta(itemMeta);
                            });
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
