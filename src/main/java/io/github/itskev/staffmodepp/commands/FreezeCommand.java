package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.manager.DataHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FreezeCommand implements CommandExecutor {

    private Plugin plugin;
    private DataHandler dataHandler;

    public FreezeCommand(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            String commandName = command.getName();
            if (args.length == 0) return false;
            Player playerToBeFrozen = plugin.getServer().getPlayer(args[0]);
            if (playerToBeFrozen != null) {
                if (commandName.equals("freeze")) {
                    dataHandler.getFreezeModule().freezePlayer(player, playerToBeFrozen);
                } else {
                    dataHandler.getFreezeModule().unfreezePlayer(player, playerToBeFrozen);
                }
            } else {
                player.sendMessage(ConfigHelper.getStringFromConfig("Freeze.PlayerNotFound", args[0]));
            }
        }
        return true;
    }
}
