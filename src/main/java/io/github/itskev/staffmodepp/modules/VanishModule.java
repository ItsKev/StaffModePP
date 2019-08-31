package io.github.itskev.staffmodepp.modules;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class VanishModule {

    private List<UUID> vanishedPlayers;

    public VanishModule() {
        vanishedPlayers = new ArrayList<>();
    }

    public List<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public void vanishPlayer(Player player) {
        vanishedPlayers.add(player.getUniqueId());
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        onlinePlayers.forEach(o -> o.hidePlayer(player));
    }

    public void unVanishPlayer(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        onlinePlayers.forEach(o -> o.showPlayer(player));
    }
}
