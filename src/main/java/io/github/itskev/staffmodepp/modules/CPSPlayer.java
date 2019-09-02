package io.github.itskev.staffmodepp.modules;

import java.util.UUID;

public class CPSPlayer {

    private int timesClicked;
    private UUID playerObserved;
    private UUID staffPlayer;
    private int secondsToObserve;

    public CPSPlayer(UUID playerObserved, UUID staffPlayer, int secondsToObserve) {
        this.playerObserved = playerObserved;
        this.staffPlayer = staffPlayer;
        this.secondsToObserve = secondsToObserve;
    }

    public int getTimesClicked() {
        return timesClicked;
    }

    public UUID getPlayerObserved() {
        return playerObserved;
    }

    public UUID getStaffPlayer() {
        return staffPlayer;
    }

    public int getSecondsToObserve() {
        return secondsToObserve;
    }

    public void increaseClickCount() {
        timesClicked += 1;
    }
}
