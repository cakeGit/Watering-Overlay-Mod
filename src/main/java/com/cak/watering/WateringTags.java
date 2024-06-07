package com.cak.watering;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**Annoyingly but understandably blocks decide at runtime if they want to allow crops or not, so here is a tag for placeable types*/
public class WateringTags {
    
    public static TagKey<Block> TILLABLE_SOILS = BlockTags.create(WateringOverlay.asResource("farmland_candidate"));
    public static TagKey<Block> FARMLAND = BlockTags.create(WateringOverlay.asResource("farmland"));
    public static TagKey<Block> SUGAR_CANE_PLACEABLE = BlockTags.create(WateringOverlay.asResource("sugar_cane_placeable"));
    public static void register() {}
    
}
