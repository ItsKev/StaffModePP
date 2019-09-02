package io.github.itskev.staffmodepp.inventory.gui;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import io.github.itskev.staffmodepp.util.ItemHelper;
import io.github.itskev.staffmodepp.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GUIAPIImpl implements GUIAPI {

    private final Plugin plugin;
    private final DataHandler dataHandler;
    private List<GUIItem> followGUIItems;

    public GUIAPIImpl(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
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
        int[] slots = {10, 11, 12, 13, 14, 15};
        for (int i = 0; i < followGUIItems.size(); i++) {
            GUIItem guiItem = followGUIItems.get(i);
            gui.addClickable(guiItem.getItemStack(), slots[i], () -> {
                player.closeInventory();
                String command = guiItem.getCommand().replace("%Player%", playerToFollow.getDisplayName());
                plugin.getServer().dispatchCommand(player, command);
            });
        }
        gui.fillBorderWith(ItemHelper.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), ChatColor.GOLD + ""));
        return gui;
    }

    @Override
    public GUI createMinersGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, "Select a Miner");
        GUI gui = new GUIImpl(inventory, plugin);
        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };
        int yNumber = plugin.getConfig().getInt("Teleport-Menu.Y-Number");
        List<? extends Player> miners = plugin.getServer().getOnlinePlayers().stream()
                .filter(p -> p.getLocation().getBlockY() <= yNumber)
                .filter(p -> !p.equals(player))
                .collect(Collectors.toList());
        boolean autoFollow = plugin.getConfig().getBoolean("Teleport-Menu.Auto-Follow");
        if (miners.size() <= slots.length) {
            for (int i = 0; i < miners.size(); i++) {
                ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                Player miner = miners.get(i);
                skullMeta.setOwner(miner.getName());
                skull.setItemMeta(skullMeta);
                gui.addClickable(skull, slots[i], () -> {
                    player.teleport(miner);
                    if (autoFollow) {
                        dataHandler.getFollowModule().toggleFollow(player, miner);
                        player.closeInventory();
                    }
                });
            }
        } else {
            //TODO: multi page inventory
        }
        gui.fillBorderWith(ItemHelper.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), ChatColor.GOLD + ""));
        return gui;
    }
}
