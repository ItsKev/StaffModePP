package io.github.itskev.staffmodepp;

import io.github.itskev.staffmodepp.commands.StaffModeCommand;
import io.github.itskev.staffmodepp.events.PlayerEvents;
import io.github.itskev.staffmodepp.manager.NoClipManager;
import io.github.itskev.staffmodepp.manager.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffModePP extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PlayerManager playerManager = new PlayerManager();
        getCommand("staffmode").setExecutor(new StaffModeCommand(this, playerManager));
        getServer().getPluginManager().registerEvents(new PlayerEvents(this, playerManager), this);

        playerManager.getNoClipPlayers().add(getServer().getOfflinePlayer("Dev_Kev").getUniqueId());
        new NoClipManager(this, playerManager).startNoClip();
    }
}
