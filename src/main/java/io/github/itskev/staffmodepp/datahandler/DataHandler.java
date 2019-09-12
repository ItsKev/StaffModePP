package io.github.itskev.staffmodepp.datahandler;

import io.github.itskev.staffmodepp.inventory.StaffInventory;
import io.github.itskev.staffmodepp.modules.*;
import io.github.itskev.staffmodepp.util.ActionBar;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class DataHandler {

    private Plugin plugin;
    private List<UUID> staffPlayers;
    private Map<UUID, Location> staffPlayerSavedLocations;
    private StaffInventory staffInventory;
    private VanishModule vanishModule;
    private NoClipModule noClipModule;
    private FreezeModule freezeModule;
    private FollowModule followModule;
    private CPSModule cpsModule;

    public DataHandler(Plugin plugin) {
        this.plugin = plugin;
        staffPlayers = new ArrayList<>();
        staffPlayerSavedLocations = new HashMap<>();
        staffInventory = new StaffInventory(plugin, this);
        vanishModule = new VanishModule(plugin, this);
        noClipModule = new NoClipModule(plugin, this);
        freezeModule = new FreezeModule(plugin);
        followModule = new FollowModule(plugin, this);
        cpsModule = new CPSModule(plugin, this);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public List<UUID> getStaffPlayers() {
        return staffPlayers;
    }

    public StaffInventory getStaffInventory() {
        return staffInventory;
    }

    public VanishModule getVanishModule() {
        return vanishModule;
    }

    public NoClipModule getNoClipModule() {
        return noClipModule;
    }

    public FreezeModule getFreezeModule() {
        return freezeModule;
    }

    public FollowModule getFollowModule() {
        return followModule;
    }

    public CPSModule getCpsModule() {
        return cpsModule;
    }

    public boolean isInStaffMode(Player player) {
        return staffPlayers.contains(player.getUniqueId());
    }

    public void addPlayerIntoStaffMode(Player player) {
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(ConfigHelper.getStringFromConfig("StaffMode.Enter"));
        staffPlayers.add(player.getUniqueId());
        staffPlayerSavedLocations.put(player.getUniqueId(), player.getLocation());
        staffInventory.saveInventory(player);
        vanishModule.vanishPlayer(player);
        ActionBar.setActionBar(player, this);
    }

    public Location removePlayerFromStaffMode(Player player) {
        player.sendMessage(ConfigHelper.getStringFromConfig("StaffMode.Leave"));
        staffPlayers.remove(player.getUniqueId());
        staffInventory.restoreInventory(player);
        vanishModule.unVanishPlayer(player);
        noClipModule.removeNoClipPlayer(player);
        player.setGameMode(GameMode.SURVIVAL);
        ActionBar.setActionBar(player, this);
        return staffPlayerSavedLocations.remove(player.getUniqueId());
    }
}
