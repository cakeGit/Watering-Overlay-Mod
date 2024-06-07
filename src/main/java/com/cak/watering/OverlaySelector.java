package com.cak.watering;

import net.minecraft.ChatFormatting;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public enum OverlaySelector {
    
    SOILS_BOX(state -> (state.is(WateringTags.TILLABLE_SOILS) || state.is(WateringTags.FARMLAND)), state -> state.is(WateringTags.SUGAR_CANE_PLACEABLE), WateringOverlay.OverlayRenderType.BOX, ChatFormatting.AQUA),
    FARMLAND_BOX(state -> state.is(WateringTags.FARMLAND), state -> state.is(WateringTags.SUGAR_CANE_PLACEABLE), WateringOverlay.OverlayRenderType.BOX, ChatFormatting.LIGHT_PURPLE),
    //CROP_ICON(state -> state.is(WateringTags.FARMLAND), state -> false, OverlayRenderType.ICON),
    OFF(null, null, null, ChatFormatting.RED);
    
    final Function<BlockState, Boolean> farmlandRangeRenderFilter;
    
    final Function<BlockState, Boolean> immediateRangeRenderFilter;
    
    final WateringOverlay.OverlayRenderType renderType;
    final ChatFormatting chatFormatting;
    
    OverlaySelector(
        Function<BlockState, Boolean> farmlandRangeRenderFilter,
        Function<BlockState, Boolean> immediateRangeRenderFilter,
        WateringOverlay.OverlayRenderType renderType,
        ChatFormatting chatFormatting
    ) {
        this.farmlandRangeRenderFilter = farmlandRangeRenderFilter;
        this.immediateRangeRenderFilter = immediateRangeRenderFilter;
        this.renderType = renderType;
        this.chatFormatting = chatFormatting;
    }
    
    public boolean shouldRenderInFarmlandRange(BlockState state) {
        return farmlandRangeRenderFilter.apply(state);
    }
    
    public boolean shouldRenderInSugarCaneRange(BlockState state) {
        return immediateRangeRenderFilter.apply(state);
    }
    
    public WateringOverlay.OverlayRenderType getRenderType() {
        return renderType;
    }
    
    public ChatFormatting getChatFormatting() {
        return chatFormatting;
    }
}
