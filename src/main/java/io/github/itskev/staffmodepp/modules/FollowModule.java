package io.github.itskev.staffmodepp.modules;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.inventory.Module;
import io.github.itskev.staffmodepp.inventory.StaffInventory;
import io.github.itskev.staffmodepp.util.ActionBar;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import io.github.itskev.staffmodepp.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;

public class FollowModule implements Listener {

    private Map<UUID, UUID> followingStaff;
    private Plugin plugin;
    private DataHandler dataHandler;
    private StaffInventory staffInventory;
    private Set<UUID> playersTeleporting = new HashSet<>();

    public FollowModule(Plugin plugin, DataHandler dataHandler) {
        followingStaff = new HashMap<>();
        this.plugin = plugin;
        this.dataHandler = dataHandler;
        this.staffInventory = dataHandler.getStaffInventory();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void toggleFollow(Player player, Player playerToFollow) {
        if (isFollowingSomeone(player)) {
            unfollowPlayer(player, playerToFollow);
        } else {
            followPlayer(player, playerToFollow);
        }
    }

    @EventHandler
    public void onPlayerInteract(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        Player playerToFollow = (Player) event.getEntity();
        if (dataHandler.isInStaffMode(player) && player.getItemInHand().getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
            event.setCancelled(true);
            followPlayer(player, playerToFollow);
        }
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {
        Entity exitedEntity = event.getEntity();
        if (exitedEntity instanceof Player) {
            Player player = (Player) exitedEntity;
            if (isFollowingSomeone(player)) {
                unfollowPlayer(player, null);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.getPassenger() != null && !playersTeleporting.contains(player.getUniqueId())) {
            event.setCancelled(true);
            playersTeleporting.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tp " + event.getPlayer().getName() +
                    " " + event.getTo().getX() + " " + event.getTo().getY() + " " + event.getTo().getZ()), 1);
            Bukkit.getScheduler().runTaskLater(plugin, () -> playersTeleporting.remove(player.getUniqueId()), 10);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isFollowingSomeone(player)) {
            unfollowPlayer(player, null);
        }
        Set<Player> players = new HashSet<>();
        for (Map.Entry<UUID, UUID> entry : followingStaff.entrySet()) {
            if (entry.getValue().equals(player.getUniqueId())) {
                players.add(player.getServer().getPlayer(entry.getKey()));
            }
        }
        players.forEach(p -> unfollowPlayer(p, null));
    }

    public boolean isFollowingSomeone(Player player) {
        return followingStaff.containsKey(player.getUniqueId());
    }

    public UUID getFollowedPlayer(Player player) {
        return followingStaff.get(player.getUniqueId());
    }

    private void followPlayer(Player player, Player playerToFollow) {
        String stringFromConfig = ConfigHelper.getStringFromConfig("Follow.Start", playerToFollow.getDisplayName());
        player.sendMessage(stringFromConfig);
        Optional<Map.Entry<Integer, Module>> follow = staffInventory.getStaffInventory().entrySet().stream().filter(entry -> entry.getValue().getModuleName().equals("Player Options")).findFirst();
        if (follow.isPresent()) {
            ItemStack skull = null;
            for (int i = 0; i < 9; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
                    skull = item;
                    break;
                }
            }
            if (skull != null) {
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setDisplayName(follow.get().getValue().getNameOn().replace("%Player%", playerToFollow.getDisplayName()));
                skullMeta.setOwner(playerToFollow.getName());
                skull.setItemMeta(skullMeta);
            }
        }
        followingStaff.put(player.getUniqueId(), playerToFollow.getUniqueId());
        playerToFollow.setPassenger(player);
        ActionBar.setActionBar(player, dataHandler);
    }

    private void unfollowPlayer(Player player, Player playerToUnfollow) {
        String name = "";
        UUID playerToUnfollowUUID = followingStaff.remove(player.getUniqueId());
        if (playerToUnfollow == null) {
            name = player.getServer().getOfflinePlayer(playerToUnfollowUUID).getName();
        } else {
            playerToUnfollow.setPassenger(null);
            player.getVehicle().eject();
            name = playerToUnfollow.getDisplayName();
        }
        String stringFromConfig = ConfigHelper.getStringFromConfig("Follow.Stop", name);
        player.sendMessage(stringFromConfig);
        Optional<Map.Entry<Integer, Module>> follow = staffInventory.getStaffInventory().entrySet().stream().filter(entry -> entry.getValue().getModuleName().equals("Player Options")).findFirst();
        if (follow.isPresent()) {
            ItemStack skull = player.getInventory().getItem(follow.get().getKey());
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(player.getName());
            skullMeta.setDisplayName(follow.get().getValue().getNameOff());
            skull.setItemMeta(skullMeta);
        }
        ActionBar.setActionBar(player, dataHandler);
    }
}
