package io.github.itskev.staffmodepp.inventory.gui;

import org.bukkit.entity.Player;

public interface GUIAPI {

    GUI createGUI(int size, String title);

    GUI createFollowGUI(Player player, Player playerToFollow);

    GUI createMinersGUI(Player player, int page);

}
