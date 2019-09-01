package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.manager.DataHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FollowCommand implements CommandExecutor {

    private Plugin plugin;
    private DataHandler dataHandler;

    public FollowCommand(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!dataHandler.isInStaffMode(player)) return true;
            if (args.length == 0) return false;
            Player playerToFollow = plugin.getServer().getPlayer(args[0]);
            if (playerToFollow != null && !player.equals(playerToFollow)) {
                dataHandler.getFollowModule().toggleFollow(player, playerToFollow);
            } else {
                player.sendMessage(ConfigHelper.getStringFromConfig("PlayerNotFound", args[0]));
            }
        }

        return true;
    }
}
