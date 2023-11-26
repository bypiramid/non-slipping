package net.bypiramid.nonslipping.engine.manager;

import net.bypiramid.nonslipping.Main;
import net.bypiramid.nonslipping.engine.block.BlockExtractor;
import net.bypiramid.nonslipping.engine.block.BlockFilter;
import net.bypiramid.nonslipping.engine.trap.Trap;
import net.bypiramid.nonslipping.listener.PlayerMoveTracker;
import net.bypiramid.nonslipping.util.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Manager {

    private Main plugin;
    private Map<UUID, Trap> trapMap;

    public Manager(Main plugin) {
        this.plugin = plugin;
        this.trapMap = new ConcurrentHashMap<>();

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerMoveTracker(this), getPlugin());
    }

    public Main getPlugin() {
        return plugin;
    }

    public Trap getTrap(Player player) {
        return trapMap.get(player.getUniqueId());
    }

    public Trap getOrCreate(Player player, Location from) {
        Trap found = trapMap.get(player.getUniqueId());

        if (found == null && (found = trackMovement(player, from)) != null) {
            trapMap.put(player.getUniqueId(), found);
        }

        return found;
    }

    public Trap removeTrap(Player player) {
        return trapMap.remove(player.getUniqueId());
    }

    /**
     * Old DetectionMethod, now improved.
     */
    public Trap trackMovement(Player player, Location from) {
        Block mainBlock = from.getBlock();

        BlockFilter north_1 = new BlockFilter(BlockFace.NORTH, 1, true);
        BlockFilter east_1 = new BlockFilter(BlockFace.EAST, 1, true);
        BlockFilter south_1 = new BlockFilter(BlockFace.SOUTH, 1, true);
        BlockFilter west_1 = new BlockFilter(BlockFace.WEST, 1, true);

        BlockFilter down_1 = new BlockFilter(BlockFace.DOWN, 1, false);
        BlockExtractor extractor = new BlockExtractor(mainBlock);

        extractor.filterAndExtract(north_1, down_1);
        extractor.filterAndExtract(east_1, down_1);
        extractor.filterAndExtract(south_1, down_1);
        extractor.filterAndExtract(west_1, down_1);

        List<Block> matchedBlocks = extractor.matchedBlocks(block -> !block.isEmpty());

        if (!matchedBlocks.isEmpty()) {
            int onGroundFences = 0;
            int foundWalls = 0;

            for (Block groundBlock : matchedBlocks) {
                Block relative_up_1 = groundBlock.getRelative(BlockFace.UP, 1);
                Block relative_up_2 = groundBlock.getRelative(BlockFace.UP, 2);

                boolean is_up1_solid = relative_up_1.getType().isSolid();
                boolean is_up2_solid = relative_up_2.getType().isSolid();

                boolean isFenceOnGround = isFenceType(groundBlock);

                if (isFenceOnGround || is_up2_solid || is_up1_solid) {
                    ++foundWalls;
                }

                if (isFenceOnGround) {
                    ++onGroundFences;
                }
            }

            if (onGroundFences == 4 || foundWalls == 4) {
                Trap trap = new Trap(player, mainBlock);

                matchedBlocks.forEach(matched -> trap.addWall(matched.getRelative(BlockFace.UP, 1)));

                return trap;
            }
        }

        return null;
    }

    public boolean isFenceType(Block block) {
        return BlockUtils.fenceTypeIds.contains(block.getType().getId());
    }
}
