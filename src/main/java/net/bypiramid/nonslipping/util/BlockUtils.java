package net.bypiramid.nonslipping.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.NumberConversions;

import java.util.Arrays;
import java.util.List;

public class BlockUtils {

    public static final List<Integer> fenceTypeIds = Arrays.asList(85, 113, 139, 183, 184, 185, 186,
            187, 188, 189, 190, 191, 192);

    public static Location getBlockCenter(Block block) {
        return block.getLocation().clone().add(0.5, 0, 0.5);
    }

    public static double x_zDistanceSquared(Location a, Location b) {
        return NumberConversions.square(a.getX() - b.getX()) + NumberConversions.square(a.getZ() - b.getZ());
    }

    public static double x_zDistance(Location a, Location b) {
        return Math.sqrt(x_zDistanceSquared(a, b));
    }

    public static Block getStuckBlock(Location playerLocation) {
        Block stuckBlock = playerLocation.getBlock().getRelative(BlockFace.UP);

        if (!stuckBlock.getType().isSolid()) {
            return null;
        }

        return stuckBlock;
    }
}
