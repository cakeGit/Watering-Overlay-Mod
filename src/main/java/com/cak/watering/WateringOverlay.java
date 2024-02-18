package com.cak.watering;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.function.Function;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WateringOverlay.MODID)
public class WateringOverlay {
    public static final String MODID = "watering_overlay";
    private static final Logger LOGGER = LogUtils.getLogger();

    public WateringOverlay() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, OverlayConfig.SPEC);
    }
    
    public static ResourceLocation asResource(String location) {
        return new ResourceLocation(MODID, location);
    }
    
    public static class DisplayOptions {
        public static int RANGE = 10;
        public static int VERTICAL_RANGE = 5;
        public static OverlaySelector SELECTOR = OverlaySelector.CROP_ICON;
    }
    
    public enum OverlaySelector {
        
        SOILS_BOX(state -> state.is(WateringTags.TILLABLE_SOILS), state -> state.is(WateringTags.SUGAR_CANE_PLACEABLE), OverlayRenderType.ICON),
        FARMLAND_BOX(state -> state.is(WateringTags.FARMLAND), state -> state.is(WateringTags.SUGAR_CANE_PLACEABLE), OverlayRenderType.ICON),
        CROP_ICON(state -> state.is(WateringTags.FARMLAND), state -> false, OverlayRenderType.ICON),
        OFF(null, null, null)
        ;
        
        final Function<BlockState, Boolean> farmlandRangeRenderFilter;
        final Function<BlockState, Boolean> immediateRangeRenderFilter;
        final OverlayRenderType renderType;
        
        OverlaySelector(
            Function<BlockState, Boolean> farmlandRangeRenderFilter,
            Function<BlockState, Boolean> immediateRangeRenderFilter,
            OverlayRenderType renderType
        ) {
            this.farmlandRangeRenderFilter = farmlandRangeRenderFilter;
            this.immediateRangeRenderFilter = immediateRangeRenderFilter;
            this.renderType = renderType;
        }
        
        public boolean shouldRenderInFarmlandRange(BlockState state) {
            return farmlandRangeRenderFilter.apply(state);
        }
        public boolean shouldRenderInImmediateRange(BlockState state) {
            return immediateRangeRenderFilter.apply(state);
        }
    
        public OverlayRenderType getRenderType() {
            return renderType;
        }
    }
    
    public enum OverlayRenderType {
        BOX, ICON
    }

}
