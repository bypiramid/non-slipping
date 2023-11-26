package net.bypiramid.nonslipping.engine.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlockIterator implements Iterator<Block> {

    private Block mainBlock;
    private List<BlockFilter> elements;
    private int index;

    private BlockIterator(Block mainBlock, List<BlockFilter> elements) {
        this.mainBlock = mainBlock;
        this.elements = elements;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean hasNext() {
        return index < elements.size();
    }

    @Override
    public Block next() {
        Block get = mainBlock;
        boolean lastSkipped = false;

        for (int i = 0; i <= index; i++) {
            BlockFilter next = elements.get(i);
            get = get.getRelative(next.getBlockFace(), next.getDistance());
            lastSkipped = next.isSkip();
        }

        ++index;

        return lastSkipped ? null : get;
    }

    public static class Builder {

        private List<BlockFilter> elements = new ArrayList<>();

        public Builder withFilters(BlockFilter... blockFilters) {
            for (BlockFilter blockFilter : blockFilters)
                elements.add(blockFilter);
            return this;
        }

        public Builder withFilter(BlockFilter blockFilter) {
            elements.add(blockFilter);
            return this;
        }

        public Builder withFilter(BlockFace blockFace, int distance, boolean skip) {
            elements.add(new BlockFilter(blockFace, distance, skip));
            return this;
        }

        public Builder withFilter(BlockFace blockFace, int distance) {
            return withFilter(blockFace, distance, false);
        }

        public BlockIterator build(Block mainBlock) {
            return new BlockIterator(mainBlock, elements);
        }
    }
}
