package io.github.itskev.staffmodepp.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ConfigHelper {

    private static FileConfiguration configuration;

    public ConfigHelper(Plugin plugin) {
        configuration = plugin.getConfig();
    }

    public static String getStringFromConfig(String nameInConfig) {
        String stringFromConfig = ChatColor.translateAlternateColorCodes('&', configuration.getString(nameInConfig));
        return PlaceholderAPI.setPlaceholders(null, stringFromConfig);
    }

    public static String getStringFromConfig(String nameInConfig, String playerName) {
        String replace = getStringFromConfig(nameInConfig).replace("%Player%", playerName);
        return PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(playerName), replace);
    }

    public static List<String> getStringListFromConfig(String nameInConfig, Player player) {
        List<String> stringList = new ArrayList<>();
        List<String> strings = configuration.getStringList(nameInConfig);
        for (String string : strings) {
            stringList.add(PlaceholderAPI.setPlaceholders(player, string));
        }
        return stringList;
    }

}
