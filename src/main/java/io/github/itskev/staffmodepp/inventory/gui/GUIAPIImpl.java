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
    public GUI createMinersGUI(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(player, 54, "Select a Miner Page " + page);
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
        List<? extends Player> minersForPage = getPlayersForPage(page, miners);
        if (page != 1 && minersForPage.isEmpty()) {
            return null;
        }
        for (int i = 0; i < minersForPage.size(); i++) {
            ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            Player miner = minersForPage.get(i);
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
        if (page > 1) {
            gui.addClickable(ItemHelper.createItem(XMaterial.ARROW.parseMaterial(), "Previous Page"),
                    48, () -> {
                        GUI minersGUI = createMinersGUI(player, page - 1);
                        minersGUI.openInventory(player);
                    });
        }
        gui.addClickable(ItemHelper.createItem(XMaterial.ARROW.parseMaterial(), "Next Page"),
                50, () -> {
                    GUI minersGUI = createMinersGUI(player, page + 1);
                    if (minersGUI != null) {
                        minersGUI.openInventory(player);
                    }
                });
        gui.fillBorderWith(ItemHelper.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), ChatColor.GOLD + ""));
        return gui;
    }

    @Override
    public GUI createStaffGUI(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(player, 54, "Current Staff Members Page " + page);
        GUI gui = new GUIImpl(inventory, plugin);
        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };
        String staffpermission = plugin.getConfig().getString("Staffpermission");
        List<? extends Player> players = plugin.getServer().getOnlinePlayers().stream()
                .filter(p -> p.hasPermission(staffpermission))
                .filter(p -> !p.equals(player))
                .collect(Collectors.toList());
        List<? extends Player> staffPlayers = getPlayersForPage(page, players);
        if (page != 1 && staffPlayers.isEmpty()) {
            return null;
        }
        for (int i = 0; i < staffPlayers.size(); i++) {
            ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            Player staffPlayer = staffPlayers.get(i);
            skullMeta.setOwner(staffPlayer.getName());
            skull.setItemMeta(skullMeta);
            gui.addClickable(skull, slots[i], () -> {
            });
        }
        if (page > 1) {
            gui.addClickable(ItemHelper.createItem(XMaterial.ARROW.parseMaterial(), "Previous Page"),
                    48, () -> {
                        GUI staffGUI = createStaffGUI(player, page - 1);
                        staffGUI.openInventory(player);
                    });
        }
        gui.addClickable(ItemHelper.createItem(XMaterial.ARROW.parseMaterial(), "Next Page"),
                50, () -> {
                    GUI staffGUI = createStaffGUI(player, page + 1);
                    if (staffGUI != null) {
                        staffGUI.openInventory(player);
                    }
                });
        gui.addClickable(ItemHelper.createItem(XMaterial.HOPPER.parseMaterial(),
                ChatColor.GOLD + "Show players in staff mode"), 45, () -> {
            GUI playersInStaffGUI = createPlayersInStaffGUI(player, 1);
            playersInStaffGUI.openInventory(player);
        });
        gui.fillBorderWith(ItemHelper.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), ChatColor.GOLD + ""));
        return gui;
    }

    private GUI createPlayersInStaffGUI(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(player, 54, "Current members in staff mode");
        GUI gui = new GUIImpl(inventory, plugin);
        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };
        List<? extends Player> players = plugin.getServer().getOnlinePlayers().stream()
                .filter(dataHandler::isInStaffMode)
                .filter(p -> !p.equals(player))
                .collect(Collectors.toList());
        List<? extends Player> staffPlayers = getPlayersForPage(page, players);
        if (page != 1 && staffPlayers.isEmpty()) {
            return null;
        }
        for (int i = 0; i < staffPlayers.size(); i++) {
            ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            Player staffPlayer = staffPlayers.get(i);
            String title = ConfigHelper.getStringFromConfig("StaffOnline.Title", staffPlayer.getDisplayName());
            skullMeta.setDisplayName(title);
            List<String> lore = plugin.getConfig().getStringList("StaffOnline.Lore");
            skullMeta.setLore(lore);
            skullMeta.setOwner(staffPlayer.getName());
            skull.setItemMeta(skullMeta);
            gui.addClickable(skull, slots[i], () -> {
            });
        }
        if (page > 1) {
            gui.addClickable(ItemHelper.createItem(XMaterial.ARROW.parseMaterial(), "Previous Page"),
                    48, () -> {
                        GUI staffGUI = createPlayersInStaffGUI(player, page - 1);
                        staffGUI.openInventory(player);
                    });
        }
        gui.addClickable(ItemHelper.createItem(XMaterial.ARROW.parseMaterial(), "Next Page"),
                50, () -> {
                    GUI staffGUI = createPlayersInStaffGUI(player, page + 1);
                    if (staffGUI != null) {
                        staffGUI.openInventory(player);
                    }
                });
        gui.addClickable(ItemHelper.createItem(XMaterial.HOPPER.parseMaterial(),
                ChatColor.GOLD + "Show current staff players"), 45, () -> {
            GUI staffGUI = createStaffGUI(player, 1);
            staffGUI.openInventory(player);
        });
        gui.fillBorderWith(ItemHelper.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), ChatColor.GOLD + ""));
        return gui;
    }

    private List<? extends Player> getPlayersForPage(int page, List<? extends Player> miners) {
        List<Player> playersOnCurrentPage = new ArrayList<>();
        for (int i = (page - 1) * 28; i < page * 28; i++) {
            if (i < miners.size()) {
                playersOnCurrentPage.add(miners.get(i));
            } else {
                break;
            }
        }
        return playersOnCurrentPage;
    }
}
