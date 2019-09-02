package io.github.itskev.staffmodepp.inventory;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.inventory.gui.GUI;
import io.github.itskev.staffmodepp.inventory.gui.GUIAPI;
import io.github.itskev.staffmodepp.inventory.gui.GUIAPIImpl;
import io.github.itskev.staffmodepp.util.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class HotBarEventHandler implements Listener {

    private static HotBarEventHandler hotBarEventHandler;

    public static HotBarEventHandler getInstance(Plugin plugin, DataHandler dataHandler) {
        if (hotBarEventHandler == null) {
            hotBarEventHandler = new HotBarEventHandler(plugin, dataHandler);
        }
        return hotBarEventHandler;
    }

    private DataHandler dataHandler;
    private Collection<Module> modules;
    private GUIAPI guiapi;

    private HotBarEventHandler(Plugin plugin, DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        modules = new ArrayList<>();
        guiapi = new GUIAPIImpl(plugin, dataHandler);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void setModules(Collection<Module> modules) {
        this.modules = modules;
    }

    @EventHandler
    public void onInventoryClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!dataHandler.isInStaffMode(player)) return;
        ItemStack item = event.getItem();
        if (item != null) {
            Optional<Module> module = modules.stream().filter(m -> m.getItemStack().equals(item)).findFirst();
            if (module.isPresent()) {
                switch (module.get().getModuleName()) {
                    case "Vanish Mode":
                        dataHandler.getVanishModule().toggleVanish(player);
                        break;
                    case "No Clip":
                        dataHandler.getNoClipModule().toggleNoClip(player);
                        break;
                    case "TP To Miners":
                        GUI minersGUI = guiapi.createMinersGUI(player);
                        minersGUI.openInventory(player);
                        break;
                }
            }
            if (item.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
                SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                if (!skullMeta.getOwner().equals(player.getName())) {
                    Player playerToFollow = player.getServer().getPlayer(skullMeta.getOwner());
                    GUI followGUI = guiapi.createFollowGUI(player, playerToFollow);
                    followGUI.openInventory(player);
                }
            }
        }
    }
}
