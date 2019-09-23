package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
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
            String staffpermission = ConfigHelper.getStringFromConfig("StaffMode.Broadcast-Permission");
            if (dataHandler.isInStaffMode(player)) {
                String message = ConfigHelper.getStringFromConfig("StaffMode.Leave-Broadcast", player.getDisplayName());
                onlinePlayers.forEach(p -> {
                    if (!p.equals(player) && p.hasPermission(staffpermission)) {
                        p.sendMessage(message);
                    }
                });
                Location location = dataHandler.removePlayerFromStaffMode(player);
                player.teleport(location);
            } else {
                String message = ConfigHelper.getStringFromConfig("StaffMode.Enter-Broadcast", player.getDisplayName());
                onlinePlayers.forEach(p -> {
                    if (!p.equals(player) && p.hasPermission(staffpermission)) {
                        p.sendMessage(message);
                    }
                });
                dataHandler.addPlayerIntoStaffMode(player);
            }
        }
        return true;
    }
}
