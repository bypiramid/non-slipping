package net.bypiramid.nonslipping.listener;

import net.bypiramid.nonslipping.engine.manager.Manager;
import net.bypiramid.nonslipping.engine.trap.Trap;
import net.bypiramid.nonslipping.util.BlockUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class PlayerMoveTracker implements Listener {

    public static final double MAX_DISTANCE_MOVABLE_INSIDE_TRAP = 0.637D;
    public static final double MAX_DISTANCE_MOVABLE_UP = 0.9D;

    public static final BlockFace[] RADIAL = {BlockFace.NORTH, BlockFace.NORTH_EAST,
            BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST,
            BlockFace.WEST, BlockFace.NORTH_WEST};

    private final Manager manager;

    public PlayerMoveTracker(Manager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();

        boolean flyEnabled = player.isFlying();
        Trap trap = manager.getOrCreate(player, from);

        if (trap == null) {
            return;
        }

        if (flyEnabled) {
            manager.removeTrap(player);
            trap.undo();
            return;
        }

        Block stuckBlock = BlockUtils.getStuckBlock(trap.getCenter().getLocation());

        int blockedPaths = 0;
        int consecutiveFences = 0;

        Block[] foundConsFences = new Block[4];
        int foundIndex = 0;

        Set<Location> notFence = new HashSet<>();

        trap.getWalls().stream().filter(block -> !manager.isFenceType(block))
                .forEach(block -> notFence.add(block.getLocation()));

        for (Block wall : trap.getWalls()) {
            Block relativeUp = wall.getRelative(BlockFace.UP);
            Block groundDown = wall.getRelative(BlockFace.DOWN);

            boolean isGroundWallFence = manager.isFenceType(groundDown);

            if (isGroundWallFence) {
                foundConsFences[foundIndex] = groundDown;
                ++foundIndex;
                ++blockedPaths;
                continue;
            }

            if (manager.isFenceType(relativeUp) || manager.isFenceType(wall) ||
                    wall.getType().isSolid() || relativeUp.getType().isSolid()) {
                blockedPaths++;
            }
        }

        if (blockedPaths == 4 || (trap.getWalls().stream().allMatch(block -> block.getType()
                .isSolid()) && stuckBlock != null)) {

            Location to = event.getTo();

            Location center;
            if (stuckBlock != null && stuckBlock.getType().isOccluding()) {
                center = BlockUtils.getBlockCenter(stuckBlock.getRelative(BlockFace.DOWN, 1));

                boolean shouldTeleport = false;

                center.setYaw(to.getYaw());
                center.setPitch(to.getPitch());

                boolean reachYLimit = from.getY() - center.getY() > MAX_DISTANCE_MOVABLE_UP;

                if (BlockUtils.x_zDistance(center, to) > MAX_DISTANCE_MOVABLE_INSIDE_TRAP) {
                    shouldTeleport = true;
                } else if ((to.getY() - center.getY()) > MAX_DISTANCE_MOVABLE_UP) {
                    shouldTeleport = true;
                }

                if (shouldTeleport) {
                    if (!player.isOnGround() && ((CraftPlayer) player).getHandle().V()) { // V() -> inWater
                        if (!reachYLimit) {
                            center.setY(from.getY());
                        } else {
                            center.setY(center.getY() + 0.4D);
                        }

                        event.setTo(center);
                    } else {
                        if (!reachYLimit) {
                            center.setY(from.getY());
                        }

                        event.setTo(center);
                    }
                }
            } else if (trap.getCenter().getRelative(BlockFace.UP, 2).getType().isSolid()) {
                if (blockedPaths == 4 && getArrayLength(foundConsFences) >= 2) {
                    BlockFace facing = yawToFace(to.getYaw()).getOppositeFace();

                    if (facing.name().contains("_")) {
                        Block base = trap.getCenter().getRelative(BlockFace.DOWN);
                        Block inDirection = base.getRelative(facing);

                        if (!inDirection.getType().isSolid()) {
                            trap.updateCenter(to.getBlock());
                            return;
                        }
                    }
                }

                if (BlockUtils.x_zDistance(center = BlockUtils.getBlockCenter(trap.getCenter()), to)
                        > MAX_DISTANCE_MOVABLE_INSIDE_TRAP) {
                    center.setYaw(to.getYaw());
                    center.setPitch(to.getPitch());
                    event.setTo(center);
                }
            } else {
                trap.updateCenter(to.getBlock());
            }
        } else {
            manager.removeTrap(player);
            trap.undo();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Trap trap = manager.removeTrap(event.getEntity());
        if (trap != null) {
            trap.undo();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        Trap trap = manager.removeTrap(event.getPlayer());
        if (trap != null) {
            trap.undo();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            Player player = event.getPlayer();
            Location to = event.getTo();
            Trap trap = manager.getTrap(player);

            if (trap != null) {
                Location center = trap.getCenter().getLocation();

                double centerY = center.getY();
                double toY = to.getY();

                double difference;
                if (toY == centerY)
                    difference = 0D;
                else if (toY > centerY) {
                    difference = toY - centerY;
                } else {
                    difference = centerY - toY;
                }

                if (BlockUtils.x_zDistance(to, center) <= 1D
                        || difference <= 1D) {
                    event.setTo(new Location(to.getWorld(), to.getX(), to.getY() + 1.5D, to.getZ(),
                            to.getYaw(), to.getPitch()));
                }

                manager.removeTrap(player);
                trap.undo();
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Trap trap = manager.removeTrap(event.getPlayer());
        if (trap != null) {
            trap.undo();
        }
    }

    public BlockFace yawToFace(float yaw) {
        return RADIAL[Math.round(yaw / 45f) & 0x7];
    }

    public <T> int getArrayLength(T[] array) {
        return (int) Stream.of(array).filter(Objects::nonNull).count();
    }
}
