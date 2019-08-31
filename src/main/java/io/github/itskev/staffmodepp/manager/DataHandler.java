package io.github.itskev.staffmodepp.manager;

import io.github.itskev.staffmodepp.inventory.StaffInventory;
import io.github.itskev.staffmodepp.modules.NoClipModule;
import io.github.itskev.staffmodepp.modules.VanishModule;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class DataHandler {

    private List<UUID> staffPlayers;
    private Map<UUID, Location> staffPlayerSavedLocations;
    private StaffInventory staffInventory;
    private VanishModule vanishModule;
    private NoClipModule noClipModule;

    public DataHandler(Plugin plugin) {
        staffPlayers = new ArrayList<>();
        staffPlayerSavedLocations = new HashMap<>();
        staffInventory = new StaffInventory(plugin, this);
        vanishModule = new VanishModule();
        noClipModule = new NoClipModule(plugin);
    }

    public VanishModule getVanishModule() {
        return vanishModule;
    }

    public NoClipModule getNoClipModule() {
        return noClipModule;
    }

    public boolean isInStaffMode(Player player) {
        return staffPlayers.contains(player.getUniqueId());
    }

    public void addPlayerIntoStaffMode(Player player) {
        player.setGameMode(GameMode.CREATIVE);
        staffPlayers.add(player.getUniqueId());
        staffPlayerSavedLocations.put(player.getUniqueId(), player.getLocation());
        staffInventory.saveInventory(player);
        vanishModule.vanishPlayer(player);
    }

    public Location removePlayerFromStaffMode(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        staffPlayers.remove(player.getUniqueId());
        staffInventory.restoreInventory(player);
        vanishModule.unVanishPlayer(player);
        noClipModule.removeNoClipPlayer(player);
        return staffPlayerSavedLocations.remove(player.getUniqueId());
    }
}
