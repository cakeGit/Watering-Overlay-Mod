package com.cak.watering;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WateringOverlay.MODID)
public class WateringOverlay {
    public static final String MODID = "watering_overlay";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public WateringOverlay() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, OverlayConfig.SPEC);
        FarmingBlockTypes.register();
    }
    
    public static ResourceLocation asResource(String location) {
        return new ResourceLocation(MODID, location);
    }
    
    public static class DisplayOptions {
        public static int RANGE = 10;
        public static int VERTICAL_RANGE = 5;
        public static OverlaySelector SELECTOR = OverlaySelector.OFF;
        public static int SELECTOR_INDEX = 2;
    }
    
    public enum OverlayRenderType {
        BOX, ICON
    }

}
