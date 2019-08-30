package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.manager.DataHandler;
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
    private DataHandler dataHandler;

    public StaffModeCommand(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
            if (dataHandler.isInStaffMode(player)) {
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("StaffMode-Leave"))
                        .replace("Player", player.getDisplayName());
                onlinePlayers.forEach(p -> {
                    if (!p.equals(player)) {
                        p.showPlayer(player);
                        p.sendMessage(message);
                    }
                });
                Location location = dataHandler.removePlayerFromStaffMode(player);
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
                dataHandler.addPlayerIntoStaffMode(player);
            }
        }
        return true;
    }
}
