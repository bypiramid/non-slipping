package net.bypiramid.nonslipping.engine.function;

import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public enum BlockTypePredicate implements Predicate<Block> {

    SOLID {
        @Override
        public boolean test(Block block) {
            return nonNull(block) && block.getType().isSolid();
        }
    },

    FENCE {
        @Override
        public boolean test(Block block) {
            return nonNull(block) && fenceTypes.contains(block.getType().getId());
        }
    },

    AIR {
        @Override
        public boolean test(Block block) {
            return nonNull(block) && block.isEmpty();
        }
    },

    LIQUID {
        @Override
        public boolean test(Block block) {
            return nonNull(block) && block.isLiquid();
        }
    },
    ;

    private static final List<Integer> fenceTypes;

    static {
        fenceTypes = Arrays.asList(85, 113, 139, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192);
    }

    private static boolean nonNull(Block block) {
        return block != null;
    }
}
