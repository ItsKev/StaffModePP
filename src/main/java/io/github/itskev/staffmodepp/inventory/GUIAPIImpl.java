package io.github.itskev.staffmodepp.inventory;

import io.github.itskev.staffmodepp.util.ConfigHelper;
import io.github.itskev.staffmodepp.util.ItemHelper;
import io.github.itskev.staffmodepp.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GUIAPIImpl implements GUIAPI {

    private final Plugin plugin;
    private List<GUIItem> followGUIItems;

    public GUIAPIImpl(Plugin plugin) {
        this.plugin = plugin;
        followGUIItems = new ArrayList<>();
        FileConfiguration config = plugin.getConfig();
        List<String> playerOptionsGUI = new ArrayList<>(config.getConfigurationSection("PlayerOptionsGUI").getKeys(false));
        playerOptionsGUI.sort(Comparator.naturalOrder());
        for (String entry : playerOptionsGUI) {
            String[] split = config.getString("PlayerOptionsGUI." + entry + ".Type").split(":");
            ItemStack itemStack;
            if (split.length > 1) {
                itemStack = new ItemStack(Integer.parseInt(split[0]), 1, Short.parseShort(split[1]));
            } else {
                itemStack = new ItemStack(Integer.parseInt(split[0]));
            }
            ItemStack item = ItemHelper.createItem(itemStack, ConfigHelper.getStringFromConfig("PlayerOptionsGUI." + entry + ".Name"),
                    config.getStringList("PlayerOptionsGUI." + entry + ".Lore").toArray(new String[0]));
            String command = ConfigHelper.getStringFromConfig("PlayerOptionsGUI." + entry + ".Command");
            followGUIItems.add(new GUIItem(item, command));
        }
    }

    @Override
    public GUI createGUI(int size, String title) {
        Inventory inventory = Bukkit.createInventory(null, size, title);
        return new GUIImpl(inventory, plugin);
    }

    @Override
    public GUI createFollowGUI(Player player, Player playerToFollow) {
        Inventory inventory = Bukkit.createInventory(player, 27, "Player options for " + playerToFollow.getDisplayName());
        GUI gui = new GUIImpl(inventory, plugin);
        int[] slots = {11, 12, 13, 14, 15, 16};
        for (int i = 0; i < followGUIItems.size(); i++) {
            GUIItem guiItem = followGUIItems.get(i);
            gui.addClickable(guiItem.getItemStack(), slots[i], () -> {
                player.closeInventory();
                String command = guiItem.getCommand().replace("%Player%", playerToFollow.getDisplayName());
                plugin.getServer().dispatchCommand(player, command);
            });
        }
        gui.fillBorderWith(ItemHelper.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), ""));
        return gui;
    }
}
