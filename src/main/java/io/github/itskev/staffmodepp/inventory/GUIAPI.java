package io.github.itskev.staffmodepp.inventory;

import org.bukkit.entity.Player;

public interface GUIAPI {

    GUI createGUI(int size, String title);

    GUI createFollowGUI(Player player, Player playerToFollow);
}
