package io.github.itskev.staffmodepp;

import io.github.itskev.staffmodepp.commands.*;
import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.events.PlayerEvents;
import io.github.itskev.staffmodepp.placeholder.PlaceHolders;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffModePP extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        new ConfigHelper(this);
        DataHandler dataHandler = new DataHandler(this);

        if (getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null) {
            new PlaceHolders(this, dataHandler).register();
        }

        //TODO: add permision
        getCommand("staffmode").setExecutor(new StaffModeCommand(this, dataHandler));
        getCommand("vanish").setExecutor(new VanishCommand(this, dataHandler));
        getCommand("noclip").setExecutor(new NoClipCommand(this, dataHandler));
        getCommand("freeze").setExecutor(new FreezeCommand(this, dataHandler));
        getCommand("unfreeze").setExecutor(new FreezeCommand(this, dataHandler));
        getCommand("follow").setExecutor(new FollowCommand(this, dataHandler));
        getCommand("cps").setExecutor(new CPSCommand(this, dataHandler));
        getCommand("staffonline").setExecutor(new StaffOnlineCommand(this, dataHandler));
        getCommand("rtpm").setExecutor(new RTPMCommand(this, dataHandler));
        getServer().getPluginManager().registerEvents(new PlayerEvents(this, dataHandler), this);
    }
}
