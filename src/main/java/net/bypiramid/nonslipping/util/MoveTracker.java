package net.bypiramid.nonslipping.util;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class MoveTracker {

    private Location tracked;

    public MoveTracker(Location tracked) {
        this.tracked = tracked;
    }

    public Location getTrackedPosition() {
        return tracked;
    }

    public Block getTrackedBlock() {
        return tracked.getBlock();
    }
}
