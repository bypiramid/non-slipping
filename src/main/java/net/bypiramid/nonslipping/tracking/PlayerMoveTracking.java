package net.bypiramid.nonslipping.tracking;

import net.bypiramid.nonslipping.Main;
import net.bypiramid.nonslipping.util.BiReference;
import net.bypiramid.nonslipping.util.BlockUtils;
import net.bypiramid.nonslipping.util.MoveTracker;
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
import org.bukkit.util.Vector;

import java.util.*;

import static org.bukkit.block.BlockFace.*;

public class PlayerMoveTracking implements Listener {

    public static final double MAX_MOVE_XZ_TRAPPED = 0.637D;
    public static final double MAX_MOVE_UP_OBSIDIAN = 0.9D;

    private static final BlockFace[] additionalFaces;

    static {
        additionalFaces = new BlockFace[]{
                BlockFace.NORTH,
                BlockFace.EAST,
                BlockFace.SOUTH,
                WEST,
        };
    }

    private Main main;

    private Map<UUID, MoveTracker> moveTrackerMap = new HashMap<>();
    private Map<UUID, Block> obsidianMap = new HashMap<>();

    public PlayerMoveTracking(Main main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!main.isEnabled()) {
            return;
        }

        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (player.isFlying()) {
            untracked(player);
            return;
        }

        Block obsidian = null;

        if (obsidianMap.containsKey(player.getUniqueId())) {
            obsidian = obsidianMap.get(player.getUniqueId());

            obsidian.getState().update();
            if (!obsidian.getType().isSolid() || !checksForTrap(obsidian.getRelative(DOWN), true)) {
                obsidianMap.remove(player.getUniqueId());
                obsidian = null;
            }
        } else if ((obsidian = BlockUtils.getStuckBlock(from)) != null
                && !checksForTrap(obsidian.getRelative(DOWN), true)) {
            obsidian = null;
        }

        if (obsidian != null) {
            obsidianMap.put(player.getUniqueId(), obsidian);

            Location center = BlockUtils.getBlockCenter(obsidian.getRelative(DOWN));

            boolean shouldTeleport = false;
            boolean reachYLimit = from.getY() - center.getY() > MAX_MOVE_UP_OBSIDIAN;

            if (BlockUtils.x_zDistance(center, to) > MAX_MOVE_XZ_TRAPPED) {
                shouldTeleport = true;
            } else if ((to.getY() - center.getY()) > MAX_MOVE_UP_OBSIDIAN) {
                shouldTeleport = true;
            }

            if (shouldTeleport) {

                center.setYaw(to.getYaw());
                center.setPitch(to.getPitch());

                if (!player.isOnGround() && ((CraftPlayer) player).getHandle().V()) { // V() -> inWater
                    if (!reachYLimit) {
                        center.setY(from.getY());
                    } else {
                        center.setY(center.getY() + 0.4D);
                    }
                } else {
                    if (!reachYLimit) {
                        center.setY(from.getY());
                    }
                }

                event.setTo(center);

                return;
            }
        }

        MoveTracker moveTracker = moveTrackerMap.get(player.getUniqueId());

        if (moveTracker != null && checksForTrap(moveTracker.getTrackedBlock(), false)) {
            double rad = (Math.toRadians(Math.toDegrees(Math.atan2((from.getZ() - to.getZ()), (from.getX() - to.getX()))) + 90));
            Location behindDirection = to.clone().setDirection(new Vector(-Math.sin(rad), 0, Math.cos(rad)));

            BlockFace facingBackwards = yawToFace(behindDirection.getYaw());
            BlockFace facingTowards = yawToFace(to.getYaw());


            if (facingBackwards.name().contains("_") && facingTowards.name().contains("_")) {
                Block base = to.getBlock().getRelative(DOWN);
                Block target;

                if (facingBackwards == facingTowards) {
                    // Player esta andando de frente
                    target = base.getRelative(facingTowards);
                } else {
                    // Player esta andando de costas
                    target = base.getRelative(facingTowards = facingBackwards);
                }

                BiReference<BlockFace, BlockFace> reference = getBlockFace(facingTowards);

                if (reference != null && !isFenceType(target)) {
                    Block face1 = target.getRelative(reference.getRefA());
                    Block face2 = target.getRelative(reference.getRefB());

                    boolean shouldBypass = true;

                    for (int i = 1; i <= 2; i++) {
                        if (face1.getRelative(UP, i).getType().isSolid()
                                || face2.getRelative(UP, i).getType().isSolid()) {
                            shouldBypass = false;
                            break;
                        }
                    }

                    if (shouldBypass && isFenceType(face1) && isFenceType(face2)) {
                        moveTrackerMap.remove(player.getUniqueId());
                        return;
                    }
                }
            }

            if (BlockUtils.x_zDistance(moveTracker.getTrackedPosition(), to) > MAX_MOVE_XZ_TRAPPED) {

                Location center = BlockUtils.getBlockCenter(moveTracker.getTrackedBlock());

                center.setYaw(to.getYaw());
                center.setPitch(to.getPitch());

                event.setTo(center);
                return;
            }
        } else {
            moveTrackerMap.remove(player.getUniqueId());
        }

        Block nextMove = to.getBlock();

        if (checksForTrap(nextMove, false)) {
            moveTrackerMap.put(player.getUniqueId(),
                    new MoveTracker(BlockUtils.getBlockCenter(nextMove)));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        untracked(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        untracked(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            Player player = event.getPlayer();

            Location target = event.getTo();
            Location newTarget = null;

            double currentYAddon = 0;

            Block obsidian = obsidianMap.get(player.getUniqueId());
            if (obsidian != null && target.distance(BlockUtils.getBlockCenter(obsidian))
                    <= MAX_MOVE_UP_OBSIDIAN) {
                newTarget = target.clone().add(0.5, currentYAddon += 1.2, 0.5);
            }

            MoveTracker moveTracker = moveTrackerMap.get(player.getUniqueId());
            if (moveTracker != null && target.distance(moveTracker.getTrackedPosition())
                    <= MAX_MOVE_XZ_TRAPPED) {
                double ySum = newTarget != null ? 0.6 : 1.2;
                double xzSum = newTarget != null ? 0.5 : 1.0;

                if (newTarget == null) {
                    newTarget = target.clone().add(xzSum, ySum, xzSum);
                } else {
                    newTarget.add(xzSum, ySum, xzSum);
                }
            }

            if (newTarget != null) {
                event.setTo(newTarget);
            }

            untracked(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        untracked(event.getPlayer());
    }

    public void untracked(Player player) {
        obsidianMap.remove(player.getUniqueId());
        moveTrackerMap.remove(player.getUniqueId());
    }

    public BlockFace yawToFace(float yaw) {
        double rotation = (yaw - 180) % 360;

        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return NORTH;
        } else if (22.5 <= rotation && rotation < 67.5) {
            return NORTH_EAST;
        } else if (67.5 <= rotation && rotation < 112.5) {
            return EAST;
        } else if (112.5 <= rotation && rotation < 157.5) {
            return SOUTH_EAST;
        } else if (157.5 <= rotation && rotation < 202.5) {
            return SOUTH;
        } else if (202.5 <= rotation && rotation < 247.5) {
            return SOUTH_WEST;
        } else if (247.5 <= rotation && rotation < 292.5) {
            return WEST;
        } else if (292.5 <= rotation && rotation < 337.5) {
            return NORTH_WEST;
        } else if (337.5 <= rotation && rotation < 360.0) {
            return NORTH;
        } else {
            return null;
        }
    }

    public BiReference<BlockFace, BlockFace> getBlockFace(BlockFace facing) {
        switch (facing) {
            case NORTH_EAST:
                return new BiReference<>(WEST, SOUTH);
            case SOUTH_EAST:
                return new BiReference<>(NORTH, WEST);
            case SOUTH_WEST:
                return new BiReference<>(EAST, NORTH);
            case NORTH_WEST:
                return new BiReference<>(SOUTH, EAST);
            default:
                return null;
        }
    }

    public boolean checksForTrap(Block center, boolean ignoreAboveBlock) {
        int onGroundFences = 0;
        int foundWalls = 0;

        for (Block groundBlock : filterGroundBlocks(center)) {
            Block relative_up_1 = groundBlock.getRelative(UP, 1);
            Block relative_up_2 = groundBlock.getRelative(UP, 2);

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

            Block aboveHead = center.getRelative(UP, 2);

            return ignoreAboveBlock || aboveHead.getType().isSolid();
        }

        return false;
    }

    public Set<Block> filterGroundBlocks(Block center) {
        Set<Block> extractions = new HashSet<>();

        for (BlockFace additional : additionalFaces) {
            Block onGround = center.getRelative(additional).getRelative(DOWN);

            if (!onGround.isEmpty()) {
                extractions.add(onGround);
            }
        }

        return extractions;
    }

    public boolean isFenceType(Block block) {
        return BlockUtils.fenceTypeIds.contains(block.getType().getId());
    }
}