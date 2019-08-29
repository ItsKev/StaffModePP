package io.github.itskev.staffmodepp;

import io.github.itskev.staffmodepp.commands.StaffModeCommand;
import io.github.itskev.staffmodepp.events.PlayerEvents;
import io.github.itskev.staffmodepp.manager.VanishedManager;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffModePP extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        VanishedManager vanishedManager = new VanishedManager();
        getCommand("staffmode").setExecutor(new StaffModeCommand(this, vanishedManager));
        getServer().getPluginManager().registerEvents(new PlayerEvents(this, vanishedManager), this);
    }
}
