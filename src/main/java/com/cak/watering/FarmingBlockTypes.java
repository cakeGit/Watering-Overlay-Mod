
package com.cak.watering;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Predicate;

/**Annoyingly but understandably blocks decide at runtime if they want to allow crops or not, so here is a "tag" for placeable types, changed to avoid conflicts when client is lan hosting*/
public class FarmingBlockTypes {
    
    public static final Predicate<BlockState> FARMLAND = blockstate -> List.of(Blocks.FARMLAND).stream().anyMatch(blockstate::is);
    public static final Predicate<BlockState> FARMLAND_CANDIDATE = blockstate -> List.of(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH, Blocks.DIRT).stream().anyMatch(blockstate::is);
    public static final Predicate<BlockState> SUGAR_CANE_PLACEABLE = blockstate -> (blockstate.is(BlockTags.DIRT) || blockstate.is(BlockTags.SAND));
    
    public static void register() {}
    
}