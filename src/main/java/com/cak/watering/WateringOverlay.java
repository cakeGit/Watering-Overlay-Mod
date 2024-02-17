package com.cak.watering;

import com.mojang.logging.LogUtils;
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
    }
    
    public static class DisplayOptions {
        static boolean ACTIVE = false;
        static int RANGE = 10;
        static int VERTICAL_RANGE = 5;
        static OverlaySelector SELECTOR = OverlaySelector.CROP_ICON;
    }
    
    public enum OverlaySelector {
        SOIL_BOX, FARMLAND_BOX, CROP_ICON
    }

}
