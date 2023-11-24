package net.bypiramid.nonslipping.engine.block;

import org.bukkit.block.Block;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockExtractor {

    private Block mainBlock;
    private Iterator<Block> blockIterator;
    private List<Block> blockExtractions = new ArrayList<>();

    public BlockExtractor(Block mainBlock, BlockFilter... blockFilters) {
        BlockIterator.Builder iteratorBuilder = BlockIterator.builder();

        for (BlockFilter blockFilter : blockFilters) {
            iteratorBuilder.withFilter(blockFilter);
        }

        this.blockIterator = iteratorBuilder.build(this.mainBlock = mainBlock);

        extractBlocks();
    }

    private void extractBlocks() {
        while (blockIterator.hasNext()) {
            Block next = blockIterator.next();
            if (next != null) {
                blockExtractions.add(next);
            }
        }
    }

    public List<Block> matchedBlocks(Predicate<Block> predicate) {
        return blockExtractions.stream().filter(predicate).collect(Collectors.toList());
    }
}
