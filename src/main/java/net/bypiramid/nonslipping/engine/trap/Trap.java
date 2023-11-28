package net.bypiramid.nonslipping.engine.trap;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Trap {

    private Player player;
    private Block center;
    private Set<Block> walls = new HashSet<>();

    public Trap(Player player, Block center) {
        this.player = player;
        this.center = center;
    }

    public Block getCenter() {
        return center;
    }

    public Set<Block> getWalls() {
        return walls;
    }

    public void setCenter(Block block) {
        this.center = block;
    }

    public void addWall(Block block) {
        walls.add(block);
    }

    public void undo() {
        center = null;

        walls.clear();
        walls = null;
    }
}
