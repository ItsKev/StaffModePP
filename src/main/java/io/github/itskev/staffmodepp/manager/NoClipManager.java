package io.github.itskev.staffmodepp.manager;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public class NoClipManager {
    private Plugin plugin;
    private List<UUID> noClipPlayers;

    public NoClipManager(Plugin plugin, DataHandler dataHandler) {
        noClipPlayers = dataHandler.getNoClipPlayers();
        this.plugin = plugin;
    }

    public void startNoClip() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkForBlocks, 0, 1);
    }

    private void checkForBlocks() {
        for (UUID noClipPlayer : noClipPlayers) {
            Player player = plugin.getServer().getPlayer(noClipPlayer);
            boolean noClip;
            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                if (player.getLocation().add(0.0D, -0.1D, 0.0D).getBlock().getType() != Material.AIR &&
                        player.isSneaking()) {
                    noClip = true;
                } else {
                    noClip = this.isNoClip(player);
                }

                if (noClip) {
                    player.setGameMode(GameMode.SPECTATOR);
                }
            } else if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (player.getLocation().add(0.0D, -0.1D, 0.0D).getBlock().getType() != Material.AIR) {
                    noClip = true;
                } else {
                    noClip = this.isNoClip(player);
                }

                if (!noClip) {
                    player.setGameMode(GameMode.CREATIVE);
                }
            }
        }
    }

    //TODO: adaptive to movement speed
    private boolean isNoClip(Player p) {
        boolean noClip = false;
        if (p.getLocation().add(1, 0.0D, 0.0D).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (p.getLocation().add(-1, 0.0D, 0.0D).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (p.getLocation().add(0.0D, 0.0D, 1).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (p.getLocation().add(0.0D, 0.0D, -1).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (p.getLocation().add(1, 1.0D, 0.0D).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (p.getLocation().add(-1, 1.0D, 0.0D).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (p.getLocation().add(0.0D, 1.0D, 1).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (p.getLocation().add(0.0D, 1.0D, -1).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (p.getLocation().add(0.0D, 1.9D, 0.0D).getBlock().getType() != Material.AIR) {
            noClip = true;
        }

        return noClip;
    }
}
