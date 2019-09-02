package io.github.itskev.staffmodepp.modules;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CPSPlayer {

    private int timesClicked;
    private int timesClickedTotal;
    private UUID playerObserved;
    private UUID staffPlayer;
    private int secondsToObserve;
    private List<Integer> clicksPerSecond;
    private BukkitTask bukkitTask;

    public CPSPlayer(UUID playerObserved, UUID staffPlayer, int secondsToObserve) {
        this.playerObserved = playerObserved;
        this.staffPlayer = staffPlayer;
        this.secondsToObserve = secondsToObserve;
        clicksPerSecond = new ArrayList<>();
    }

    public int getTimesClicked() {
        return timesClickedTotal;
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

    public void startCounter(Plugin plugin) {
        bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            clicksPerSecond.add(timesClicked);
            timesClicked = 0;
        }, 20, 20);
    }

    public List<Integer> stopTaskAndGetResult() {
        bukkitTask.cancel();
        return clicksPerSecond;
    }

    public void increaseClickCount() {
        timesClicked += 1;
        timesClickedTotal += 1;
    }
}
