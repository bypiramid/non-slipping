package net.bypiramid.nonslipping.engine.trap;

import net.bypiramid.nonslipping.util.Cooldown;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.MaterialData;

public class Trap {

    public static final double MAX_DISTANCE_MOVABLE_INSIDE_TRAP = 0.637D;
    public static final double MAX_DISTANCE_MOVABLE_INSIDE_TRAP_WITH_BLOCK_IN_FACE = 0.9D;

    private Player player;
    private Location center;
    private MaterialData inputBlockData;
    private Block stuckBlock;
    private Cooldown teleportCooldown;

    public Trap(Player player, Location center, MaterialData inputBlockData, Block stuckBlock) {
        this.player = player;
        this.center = center;
        this.inputBlockData = inputBlockData;
        this.stuckBlock = stuckBlock;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public MaterialData getInputBlockData() {
        return inputBlockData;
    }

    public void setMaterialData(MaterialData inputBlockData) {
        this.inputBlockData = inputBlockData;
    }

    public Block getStuckBlock() {
        return stuckBlock;
    }

    public boolean hasStuckBlock() {
        return stuckBlock != null;
    }

    public void setStuckBlock(Block stuckBlock) {
        this.stuckBlock = stuckBlock;
    }

    public boolean exists() {
        return false;
    }

    public boolean moveToCenter() {
        if (!player.isOnline()) {
            return false;
        }

        if (teleportCooldown != null && !teleportCooldown.expired()) {
            return false;
        }

        teleportCooldown = new Cooldown(0.3);
        player.teleport(center, PlayerTeleportEvent.TeleportCause.PLUGIN);
        return false;
    }
}
