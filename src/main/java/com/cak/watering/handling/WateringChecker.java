package com.cak.watering.handling;

import com.cak.watering.WateringOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WateringChecker {
    
    static boolean hasBounds = false;
    static BoundingBox WATER_BOUNDS = new BoundingBox(0, 0, 0, 0, 0, 0);
   
    
    
    public static boolean isInside(BlockPos blockPos) {
        return hasBounds && WATER_BOUNDS.isInside(blockPos);
    }
    
}
