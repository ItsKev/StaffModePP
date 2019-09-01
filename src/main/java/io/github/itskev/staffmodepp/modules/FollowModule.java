package io.github.itskev.staffmodepp.modules;

import io.github.itskev.staffmodepp.inventory.Module;
import io.github.itskev.staffmodepp.inventory.StaffInventory;
import io.github.itskev.staffmodepp.manager.DataHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FollowModule implements Listener {

    private Map<UUID, UUID> followingStaff;
    private DataHandler dataHandler;
    private StaffInventory staffInventory;

    public FollowModule(Plugin plugin, DataHandler dataHandler) {
        followingStaff = new HashMap<>();
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
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        Player playerToFollow = (Player) event.getEntity();
        if (dataHandler.isInStaffMode(player)) {
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

    private boolean isFollowingSomeone(Player player) {
        return followingStaff.containsKey(player.getUniqueId());
    }

    private void followPlayer(Player player, Player playerToFollow) {
        String stringFromConfig = ConfigHelper.getStringFromConfig("Follow-Start", playerToFollow.getDisplayName());
        player.sendMessage(stringFromConfig);
        Optional<Map.Entry<Integer, Module>> follow = staffInventory.getStaffInventory().entrySet().stream().filter(entry -> entry.getValue().getModuleName().equals("Follow")).findFirst();
        if (follow.isPresent()) {
            ItemStack skull = player.getInventory().getItem(follow.get().getKey());
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(playerToFollow.getName());
            skull.setItemMeta(skullMeta);
        }
        followingStaff.put(player.getUniqueId(), playerToFollow.getUniqueId());
        playerToFollow.setPassenger(player);
    }

    private void unfollowPlayer(Player player, Player playerToUnfollow) {
        String name = "";
        UUID playerToUnfollowUUID = followingStaff.remove(player.getUniqueId());
        if (playerToUnfollow == null) {
            name = player.getServer().getOfflinePlayer(playerToUnfollowUUID).getName();
        } else {
            playerToUnfollow.setPassenger(null);
            name = playerToUnfollow.getDisplayName();
        }
        String stringFromConfig = ConfigHelper.getStringFromConfig("Follow-Stop", name);
        player.sendMessage(stringFromConfig);
        Optional<Map.Entry<Integer, Module>> follow = staffInventory.getStaffInventory().entrySet().stream().filter(entry -> entry.getValue().getModuleName().equals("Follow")).findFirst();
        if (follow.isPresent()) {
            ItemStack skull = player.getInventory().getItem(follow.get().getKey());
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(player.getName());
            skull.setItemMeta(skullMeta);
        }
    }
}
