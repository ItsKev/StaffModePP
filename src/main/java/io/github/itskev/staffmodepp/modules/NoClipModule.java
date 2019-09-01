package io.github.itskev.staffmodepp.modules;

import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NoClipModule {
    private Plugin plugin;
    private List<UUID> noClipPlayers;
    private List<BlockFace> directions;

    private BukkitTask noClipTask;

    public NoClipModule(Plugin plugin) {
        noClipPlayers = new ArrayList<>();
        this.plugin = plugin;
        directions = Arrays.asList(BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST);
    }

    public boolean isInNoClipMode(Player player) {
        return noClipPlayers.contains(player.getUniqueId());
    }

    public void addNoClipPlayer(Player player) {
        if (noClipTask == null) {
            startNoClipTask();
        }
        player.sendMessage(ConfigHelper.getStringFromConfig("NoClip-Enter"));
        noClipPlayers.add(player.getUniqueId());
    }

    public void removeNoClipPlayer(Player player) {
        player.sendMessage(ConfigHelper.getStringFromConfig("NoClip-Leave"));
        noClipPlayers.remove(player.getUniqueId());
        if (noClipPlayers.isEmpty() && noClipTask != null) {
            stopNoClipTask();
        }
    }

    private void startNoClipTask() {
        noClipTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::checkForBlocks, 0, 1);
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
                if (player.getLocation().getBlock().getType() != Material.AIR &&
                        player.isSneaking()) {
                    noClip = true;
                } else {
                    noClip = this.isNoClip(player);
                }

                if (noClip) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> player.setGameMode(GameMode.SPECTATOR));
                }
            } else if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (player.getLocation().getBlock().getType() != Material.AIR) {
                    noClip = true;
                } else {
                    noClip = this.isNoClip(player);
                }

                if (!noClip) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> player.setGameMode(GameMode.CREATIVE));
                }
            }
        }
    }

    private boolean isNoClip(Player player) {
        List<Location> playerLocations = Arrays.asList(player.getLocation(),
                player.getLocation().add(0.0D, 1.0D, 0.0D),
                player.getLocation().add(0.0D, 2.0D, 0.0D));

        for (Location playerLocation : playerLocations) {
            for (BlockFace blockFace : directions) {
                if (playerLocation.getBlock().getRelative(blockFace).getType() != Material.AIR) {
                    return true;
                }
            }
        }
        return false;
    }
}
