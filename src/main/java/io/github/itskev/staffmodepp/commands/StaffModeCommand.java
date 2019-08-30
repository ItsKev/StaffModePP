package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.manager.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class StaffModeCommand implements CommandExecutor {

    private Plugin plugin;
    private PlayerManager playerManager;

    public StaffModeCommand(Plugin plugin, PlayerManager playerManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
            if (playerManager.isInStaffMode(player)) {
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("StaffMode-Leave"))
                        .replace("Player", player.getDisplayName());
                onlinePlayers.forEach(p -> {
                    if (!p.equals(player)) {
                        p.showPlayer(player);
                        p.sendMessage(message);
                    }
                });
                Location location = playerManager.removePlayerFromStaffMode(player);
                player.teleport(location);
            } else {
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("StaffMode-Enter"))
                        .replace("Player", player.getDisplayName());
                onlinePlayers.forEach(p -> {
                    if (!p.equals(player)) {
                        p.hidePlayer(player);
                        p.sendMessage(message);
                    }
                });
                playerManager.addPlayerIntoStaffMode(player);
            }
        }
        return true;
    }
}
