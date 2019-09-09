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
        new ConfigHelper(this);
        DataHandler dataHandler = new DataHandler(this);

        if (getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null) {
            new PlaceHolders(this, dataHandler).register();
        }

        String staffpermission = getConfig().getString("Staffpermission");

        getCommand("staffmode").setExecutor(new StaffModeCommand(this, dataHandler));
        getCommand("staffmode").setPermission(staffpermission);
        getCommand("vanish").setExecutor(new VanishCommand(this, dataHandler));
        getCommand("vanish").setPermission(staffpermission);
        getCommand("noclip").setExecutor(new NoClipCommand(this, dataHandler));
        getCommand("noclip").setPermission(staffpermission);
        getCommand("freeze").setExecutor(new FreezeCommand(this, dataHandler));
        getCommand("freeze").setPermission(staffpermission);
        getCommand("unfreeze").setExecutor(new FreezeCommand(this, dataHandler));
        getCommand("unfreeze").setPermission(staffpermission);
        getCommand("follow").setExecutor(new FollowCommand(this, dataHandler));
        getCommand("follow").setPermission(staffpermission);
        getCommand("cps").setExecutor(new CPSCommand(this, dataHandler));
        getCommand("cps").setPermission(staffpermission);
        getCommand("staffonline").setExecutor(new StaffOnlineCommand(this, dataHandler));
        getCommand("staffonline").setPermission(staffpermission);
        getCommand("rtpm").setExecutor(new RTPMCommand(this, dataHandler));
        getCommand("rtpm").setPermission(staffpermission);
        getServer().getPluginManager().registerEvents(new PlayerEvents(this, dataHandler), this);
    }
}
