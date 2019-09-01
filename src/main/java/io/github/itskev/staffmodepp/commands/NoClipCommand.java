package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.manager.DataHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NoClipCommand implements CommandExecutor {

    private Plugin plugin;
    private DataHandler dataHandler;

    public NoClipCommand(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (dataHandler.isInStaffMode(player)) {
                dataHandler.getNoClipModule().toggleNoClip(player);
            }
        }
        return true;
    }
}
