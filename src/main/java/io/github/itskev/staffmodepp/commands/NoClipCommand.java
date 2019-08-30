package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.manager.DataHandler;
import io.github.itskev.staffmodepp.modules.NoClipModule;
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
                NoClipModule noClipModule = dataHandler.getNoClipModule();
                if(noClipModule.isInNoClipMode(player)) {
                    noClipModule.removeNoClipPlayer(player);
                } else {
                    noClipModule.addNoClipPlayer(player);
                }
            }
        }
        return true;
    }
}
