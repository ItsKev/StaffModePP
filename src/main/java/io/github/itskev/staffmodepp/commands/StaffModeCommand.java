package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.manager.VanishedManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class StaffModeCommand implements CommandExecutor {

    private List<UUID> vanishedStaff;
    private Plugin plugin;

    public StaffModeCommand(Plugin plugin, VanishedManager vanishedManager) {
        vanishedStaff = vanishedManager.getVanishedStaff();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            UUID playerUUID = player.getUniqueId();
            Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
            if (vanishedStaff.contains(playerUUID)) {
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("StaffMode-Leave"))
                        .replace("Player", player.getDisplayName());
                onlinePlayers.forEach(p -> {
                    if (!p.getUniqueId().equals(playerUUID)) {
                        p.showPlayer(player);
                        p.sendMessage(message);
                    }
                });
                vanishedStaff.remove(playerUUID);
            } else {
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("StaffMode-Enter"))
                        .replace("Player", player.getDisplayName());
                onlinePlayers.forEach(p -> {
                    if (!p.getUniqueId().equals(playerUUID)) {
                        p.hidePlayer(player);
                        p.sendMessage(message);
                    }
                });
                vanishedStaff.add(playerUUID);
            }
        }
        return true;
    }
}
