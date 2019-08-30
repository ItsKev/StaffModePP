package io.github.itskev.staffmodepp.manager;

import io.github.itskev.staffmodepp.inventory.StaffInventory;
import io.github.itskev.staffmodepp.modules.VanishModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class DataHandler {

    private List<UUID> staffPlayers;
    private Map<UUID, Location> staffPlayerSavedLocations;
    private StaffInventory staffInventory;
    private VanishModule vanishModule;

    public DataHandler() {
        staffPlayers = new ArrayList<>();
        staffPlayerSavedLocations = new HashMap<>();
        staffInventory = new StaffInventory();
        vanishModule = new VanishModule();
    }

    public VanishModule getVanishModule() {
        return vanishModule;
    }

    public boolean isInStaffMode(Player player) {
        return staffPlayers.contains(player.getUniqueId());
    }

    public void addPlayerIntoStaffMode(Player player) {
        staffPlayers.add(player.getUniqueId());
        staffPlayerSavedLocations.put(player.getUniqueId(), player.getLocation());
        staffInventory.saveInventory(player);
        vanishModule.vanishPlayer(player);
    }

    public Location removePlayerFromStaffMode(Player player) {
        staffPlayers.remove(player.getUniqueId());
        staffInventory.restoreInventory(player);
        vanishModule.unVanishPlayer(player);
        return staffPlayerSavedLocations.remove(player.getUniqueId());
    }
}
