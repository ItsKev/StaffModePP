package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.inventory.gui.GUIAPI;
import io.github.itskev.staffmodepp.inventory.gui.GUIAPIImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class StaffOnlineCommand implements CommandExecutor {

    private Plugin plugin;
    private DataHandler dataHandler;
    private GUIAPI guiapi;

    public StaffOnlineCommand(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
        guiapi = new GUIAPIImpl(plugin, dataHandler);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!dataHandler.isInStaffMode(player)) return true;
            guiapi.createStaffGUI(player, 1);
        }
        return false;
    }
}
