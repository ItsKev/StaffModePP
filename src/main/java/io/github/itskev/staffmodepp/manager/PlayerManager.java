package io.github.itskev.staffmodepp.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private List<UUID> staffPlayers;
    private Map<UUID, Location> staffPlayerSavedLocations;
    private List<UUID> vanishedPlayers;
    private List<UUID> frozenPlayers;
    private List<UUID> noClipPlayers;

    public PlayerManager() {
        staffPlayers = new ArrayList<>();
        staffPlayerSavedLocations = new HashMap<>();
        vanishedPlayers = new ArrayList<>();
        noClipPlayers = new ArrayList<>();
    }

    public List<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public List<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    public List<UUID> getNoClipPlayers() {
        return noClipPlayers;
    }

    public boolean isInStaffMode(Player player) {
        return staffPlayers.contains(player.getUniqueId());
    }

    public void addPlayerIntoStaffMode(Player player) {
        staffPlayers.add(player.getUniqueId());
        staffPlayerSavedLocations.put(player.getUniqueId(), player.getLocation());
    }

    public Location removePlayerFromStaffMode(Player player) {
        staffPlayers.remove(player.getUniqueId());
        return staffPlayerSavedLocations.remove(player.getUniqueId());
    }
}
