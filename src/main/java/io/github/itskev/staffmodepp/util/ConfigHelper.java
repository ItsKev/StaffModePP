package io.github.itskev.staffmodepp.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigHelper {

    private static FileConfiguration configuration;

    public ConfigHelper(Plugin plugin) {
        configuration = plugin.getConfig();
    }

    public static String getStringFromConfig(String nameInConfig) {
        return ChatColor.translateAlternateColorCodes('&', configuration.getString(nameInConfig));
    }

    public static String getStringFromConfig(String nameInConfig, String playerName) {
        return getStringFromConfig(nameInConfig).replace("%Player%", playerName);
    }

}
