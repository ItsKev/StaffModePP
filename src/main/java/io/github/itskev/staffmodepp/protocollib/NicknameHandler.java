package io.github.itskev.staffmodepp.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NicknameHandler {

    private static NicknameHandler nicknameHandler;

    public static NicknameHandler getInstance(Plugin plugin) {
        if (nicknameHandler == null) {
            nicknameHandler = new NicknameHandler(plugin);
        }
        return nicknameHandler;
    }

    private Map<UUID, String> playerNames;

    private NicknameHandler(Plugin plugin) {
        playerNames = new HashMap<>();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.PLAYER_INFO) {

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerPlayerInfo wrapper = new WrapperPlayServerPlayerInfo(event.getPacket());
                List<PlayerInfoData> playerInfoDataList = wrapper.getData();

                if (wrapper.getAction() != EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                    return;
                }

                List<PlayerInfoData> newPlayerInfoDataList = Lists.newArrayList();

                for (PlayerInfoData playerInfoData : playerInfoDataList) {
                    Player player;

                    if (playerInfoData == null || playerInfoData.getProfile() == null || (player = Bukkit.getPlayer(playerInfoData.getProfile().getUUID())) == null || !player.isOnline()) {
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }

                    WrappedGameProfile profile = playerInfoData.getProfile();

                    String newNick = playerNames.get(player.getUniqueId());
                    if (newNick == null) {
                        newNick = player.getName();
                    }
                    if (newNick.length() > 16) {
                        newNick = newNick.substring(0, 16);
                    }

                    WrappedGameProfile newProfile = profile.withName(newNick);
                    newProfile.getProperties().putAll(profile.getProperties());

                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(newProfile, playerInfoData.getPing(), playerInfoData.getGameMode(), playerInfoData.getDisplayName());
                    newPlayerInfoDataList.add(newPlayerInfoData);
                }

                wrapper.setData(newPlayerInfoDataList);
            }
        });
    }

    public void addCustomPlayerName(Player player, String name) {
        playerNames.put(player.getUniqueId(), name);
    }

    public void removeCustomPlayerName(Player player) {
        playerNames.remove(player.getUniqueId());
    }
}
