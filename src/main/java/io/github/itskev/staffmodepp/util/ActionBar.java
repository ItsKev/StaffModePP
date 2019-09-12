package io.github.itskev.staffmodepp.util;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ActionBar {

    private static Map<UUID, String> actionBarPlayers = new HashMap<>();
    private static BukkitTask task;

    public static void setActionBar(Player player, DataHandler dataHandler) {
        if (task == null) {
            task = Bukkit.getScheduler().runTaskTimer(dataHandler.getPlugin(), ActionBar::updateBar, 0, 5);
        }
        int staffModePrio = Integer.parseInt(ConfigHelper.getStringFromConfig("StaffModeActionBar.Priority"));
        String staffModeText = ConfigHelper.getStringFromConfig("StaffModeActionBar.Text");
        int vanishPrio = Integer.parseInt(ConfigHelper.getStringFromConfig("VanishActionBar.Priority"));
        String vanishText = ConfigHelper.getStringFromConfig("VanishActionBar.Text");
        int followPrio = Integer.parseInt(ConfigHelper.getStringFromConfig("FollowingActionBar.Priority"));
        if (dataHandler.isInStaffMode(player)) {
            if (dataHandler.getVanishModule().isVanished(player)) {
                if (dataHandler.getFollowModule().isFollowingSomeone(player)) {
                    if (staffModePrio < vanishPrio && staffModePrio < followPrio) {
                        sendActionbar(player, staffModeText);
                    } else if (vanishPrio < followPrio) {
                        sendActionbar(player, vanishText);
                    } else {
                        String name = Bukkit.getPlayer(dataHandler.getFollowModule().getFollowedPlayer(player)).getName();
                        String followText = ConfigHelper.getStringFromConfig("FollowingActionBar.Text", name);
                        sendActionbar(player, followText);
                    }
                } else {
                    if (staffModePrio < vanishPrio) {
                        sendActionbar(player, staffModeText);
                    } else {
                        sendActionbar(player, vanishText);
                    }
                }
            } else {
                if (dataHandler.getFollowModule().isFollowingSomeone(player)) {
                    if (staffModePrio < vanishPrio) {
                        sendActionbar(player, staffModeText);
                    } else {
                        String name = Bukkit.getPlayer(dataHandler.getFollowModule().getFollowedPlayer(player)).getName();
                        String followText = ConfigHelper.getStringFromConfig("FollowingActionBar.Text", name);
                        sendActionbar(player, followText);
                    }
                } else {
                    sendActionbar(player, staffModeText);
                }
            }
        } else {
            if (dataHandler.getVanishModule().isVanished(player)) {
                if (dataHandler.getFollowModule().isFollowingSomeone(player)) {
                    if (vanishPrio < followPrio) {
                        sendActionbar(player, vanishText);
                    } else {
                        String name = Bukkit.getPlayer(dataHandler.getFollowModule().getFollowedPlayer(player)).getName();
                        String followText = ConfigHelper.getStringFromConfig("FollowingActionBar.Text", name);
                        sendActionbar(player, followText);
                    }
                } else {
                    sendActionbar(player, vanishText);
                }
            } else {
                if (dataHandler.getFollowModule().isFollowingSomeone(player)) {
                    String name = Bukkit.getPlayer(dataHandler.getFollowModule().getFollowedPlayer(player)).getName();
                    String followText = ConfigHelper.getStringFromConfig("FollowingActionBar.Text", name);
                    sendActionbar(player, followText);
                } else {
                    actionBarPlayers.remove(player.getUniqueId());
                }
            }
        }
    }

    private static void sendActionbar(Player player, String message) {
        actionBarPlayers.put(player.getUniqueId(), message);
    }

    private static void updateBar() {
        Set<UUID> playerToRemove = new HashSet<>();
        actionBarPlayers.forEach((uuid, s) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                setBar(player, s);
            } else {
                playerToRemove.add(uuid);
            }
        });
        playerToRemove.forEach(uuid -> actionBarPlayers.remove(uuid));
    }

    private static void setBar(Player player, String message) {
        if (player == null || message == null) return;
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

        //1.10 and up
        if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        }

        //1.8.x and 1.9.x
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            Object packetPlayOutChat;
            Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

            Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
            packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

            Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
