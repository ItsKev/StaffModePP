package io.github.itskev.staffmodepp.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VanishedManager {

    private List<UUID> vanishedStaff;

    public VanishedManager() {
        vanishedStaff = new ArrayList<>();
    }

    public List<UUID> getVanishedStaff() {
        return vanishedStaff;
    }
}
