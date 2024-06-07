package com.cak.watering;

import net.minecraft.resources.ResourceLocation;

public enum WateredType {
    FARMLAND(WateringOverlay.asResource("textures/water_overlay.png")),
    SUGAR_CANE_ONLY(WateringOverlay.asResource("textures/water_sugar_cane_overlay.png"));
    
    final ResourceLocation texture;
    
    WateredType(ResourceLocation texture) {
        this.texture = texture;
    }
    
    public ResourceLocation getTexture() {
        return texture;
    }
}
