package io.github.itskev.staffmodepp.placeholder;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceHolders extends PlaceholderExpansion {

    private Plugin plugin;
    private DataHandler dataHandler;

    public PlaceHolders(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "staffmodeplusplus";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String value) {
        if (value.equals("amountStaffmode")) {
            return Integer.toString(dataHandler.getStaffPlayers().size());
        }

        if (value.equals("amountFrozen")) {
            return Integer.toString(dataHandler.getFreezeModule().amountFrozen());
        }

        if (value.equals("amountUnderground")) {
            int yNumber = plugin.getConfig().getInt("Teleport-Menu.Y-Number");
            List<? extends Player> miners = plugin.getServer().getOnlinePlayers().stream()
                    .filter(p -> p.getLocation().getBlockY() <= yNumber)
                    .collect(Collectors.toList());
            return Integer.toString(miners.size());
        }

        if (player == null) {
            return "";
        }

        if (value.equals("inStaffmode")) {
            return dataHandler.isInStaffMode(player.getPlayer()) ? "True" : "False";
        }

        if (value.equals("isVanished")) {
            return dataHandler.getVanishModule().isVanished(player.getPlayer()) ? "True" : "False";
        }

        return null;
    }
}
