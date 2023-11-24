package net.bypiramid.nonslipping.util;

import net.bypiramid.nonslipping.engine.function.BlockTypePredicate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.NumberConversions;

public class BlockUtils {

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

        if (!BlockTypePredicate.SOLID.test(stuckBlock)) {
            return null;
        }

        return stuckBlock;
    }
}
