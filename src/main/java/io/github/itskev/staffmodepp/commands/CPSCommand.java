package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CPSCommand implements CommandExecutor {

    private Plugin plugin;
    private DataHandler dataHandler;

    public CPSCommand(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            String permission = ConfigHelper.getStringFromConfig("UseCommandsWithoutStaffMode.Permission");
            if (!player.hasPermission(permission)) {
                if (!dataHandler.isInStaffMode(player)) {
                    String stringFromConfig = ConfigHelper.getStringFromConfig("UseCommandsWithoutStaffMode.MessageWithNoPermission");
                    player.sendMessage(stringFromConfig);
                    return true;
                }
            }
            if (args.length < 1) return false;
            Player cpsPlayer = plugin.getServer().getPlayer(args[0]);
            if (cpsPlayer != null) {
                int duration = 10;
                if (args.length >= 2) {
                    try {
                        duration = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.BLACK + args[1] + ChatColor.RED + " is not a valid number!");
                    }
                }
                dataHandler.getCpsModule().startCPSForPlayer(cpsPlayer, player, duration);
            } else {
                player.sendMessage(ConfigHelper.getStringFromConfig("PlayerNotFound", args[0]));
            }
        }
        return true;
    }
}
