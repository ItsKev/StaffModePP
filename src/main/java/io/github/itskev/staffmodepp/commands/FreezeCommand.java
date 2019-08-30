package io.github.itskev.staffmodepp.commands;

import io.github.itskev.staffmodepp.manager.DataHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class FreezeCommand implements CommandExecutor {

    private List<UUID> frozenPlayers;

    public FreezeCommand(DataHandler dataHandler) {
        frozenPlayers = dataHandler.getFrozenPlayers();
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (s.equals("freeze")) {

            } else {

            }
        }
        return true;
    }
}
