package net.bypiramid.nonslipping.engine.block;

import org.bukkit.block.BlockFace;

public class BlockFilter {

    private final BlockFace blockFace;
    private final int distance;
    private final boolean skip;

    public BlockFilter(BlockFace blockFace, int distance, boolean skip) {
        this.blockFace = blockFace;
        this.distance = distance;
        this.skip = skip;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isSkip() {
        return skip;
    }
}
