package io.github.itskev.staffmodepp;

import io.github.itskev.staffmodepp.commands.StaffModeCommand;
import io.github.itskev.staffmodepp.commands.VanishCommand;
import io.github.itskev.staffmodepp.events.PlayerEvents;
import io.github.itskev.staffmodepp.manager.DataHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffModePP extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        DataHandler dataHandler = new DataHandler();
        getCommand("staffmode").setExecutor(new StaffModeCommand(this, dataHandler));
        getCommand("vanish").setExecutor(new VanishCommand(this, dataHandler));
        getServer().getPluginManager().registerEvents(new PlayerEvents(this, dataHandler), this);
    }
}
