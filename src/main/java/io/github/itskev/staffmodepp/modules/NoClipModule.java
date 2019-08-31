package io.github.itskev.staffmodepp.modules;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class NoClipModule {
    private Plugin plugin;
    private List<UUID> noClipPlayers;
    private Map<UUID, Location> lastLocation;

    private BukkitTask noClipTask;

    public NoClipModule(Plugin plugin) {
        noClipPlayers = new ArrayList<>();
        this.plugin = plugin;
        lastLocation = new HashMap<>();
    }

    public boolean isInNoClipMode(Player player) {
        return noClipPlayers.contains(player.getUniqueId());
    }

    public void addNoClipPlayer(Player player) {
        if (noClipTask == null) {
            startNoClipTask();
        }
        noClipPlayers.add(player.getUniqueId());
        lastLocation.put(player.getUniqueId(), player.getLocation());
    }

    public void removeNoClipPlayer(Player player) {
        noClipPlayers.remove(player.getUniqueId());
        lastLocation.remove(player.getUniqueId());
        if (noClipPlayers.isEmpty()) {
            stopNoClipTask();
        }
    }

    private void startNoClipTask() {
        noClipTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkForBlocks, 0, 2);
    }

    private void stopNoClipTask() {
        noClipTask.cancel();
        noClipTask = null;
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

    private boolean isNoClip(Player player) {
        double speed = lastLocation.remove(player.getUniqueId()).distance(player.getLocation()) * 5;
        lastLocation.put(player.getUniqueId(), player.getLocation());
        speed = Math.min(Math.max(0.8d, speed), 2d);
        boolean noClip = false;
        if (player.getLocation().add(1 * speed, 0.0D, 0.0D).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (player.getLocation().add(-1 * speed, 0.0D, 0.0D).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (player.getLocation().add(0.0D, 0.0D, 1 * speed).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (player.getLocation().add(0.0D, 0.0D, -1 * speed).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (player.getLocation().add(1 * speed, 1.0D, 0.0D).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (player.getLocation().add(-1 * speed, 1.0D, 0.0D).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (player.getLocation().add(0.0D, 1.0D, 1 * speed).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (player.getLocation().add(0.0D, 1.0D, -1 * speed).getBlock().getType() != Material.AIR) {
            noClip = true;
        } else if (player.getLocation().add(0.0D, 1.9D, 0.0D).getBlock().getType() != Material.AIR) {
            noClip = true;
        }
        return noClip;
    }
}
