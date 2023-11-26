package net.bypiramid.nonslipping.engine.block;

import org.bukkit.block.Block;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BlockExtractor {

    private Block mainBlock;
    private List<Block> blockExtractions = new ArrayList<>();

    public BlockExtractor(Block mainBlock) {
        this.mainBlock = mainBlock;
    }

    public void filterAndExtract(BlockFilter... blockFilters) {
        BlockIterator blockIterator = BlockIterator.builder().withFilters(blockFilters)
                .build(mainBlock);
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
