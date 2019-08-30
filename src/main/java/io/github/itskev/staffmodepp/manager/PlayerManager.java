package io.github.itskev.staffmodepp.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private List<UUID> vanishedStaff;
    private List<UUID> vanishedPlayers;
    private List<UUID> frozenPlayers;
    private List<UUID> noClipPlayers;

    public PlayerManager() {
        vanishedStaff = new ArrayList<>();
        vanishedPlayers = new ArrayList<>();
        noClipPlayers = new ArrayList<>();
    }

    public List<UUID> getVanishedStaff() {
        return vanishedStaff;
    }

    public List<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public List<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    public List<UUID> getNoClipPlayers() {
        return noClipPlayers;
    }
}
