package com.cak.watering.handling;

import com.cak.watering.WateringOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WateringChecker {
    
    /**Used for tracking farmland hydration*/
    static HashSet<BlockPos> FARMLAND_RANGE_BLOCKS;
    /**Used for tracking sugar cane hydration*/
    static HashSet<BlockPos> IMMEDIATE_HYDRATION_BLOCKS;
    
    static boolean hasBounds = false;
    static BoundingBox WATER_BOUNDS = new BoundingBox(0, 0, 0, 0, 0, 0);
    
    static int LAZY_TICK_INTERVAL = 20;
    static int lazyTick = 0;
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        lazyTick++;
        if (lazyTick == LAZY_TICK_INTERVAL) {
            lazyTick = 0;
    
            Minecraft mc = Minecraft.getInstance();
            Level level = mc.level;
            
            if (mc.level != null && mc.player != null)
                updateWatering(mc.level, mc.player);
        }
    }
    
    static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST};
    static final int WATER_RANGE = 4;
    
    public static void updateWatering(Level level, LocalPlayer player) {
        BlockPos originPos = BlockPos.containing(player.position());
        
        int updateRange = WateringOverlay.DisplayOptions.RANGE + WATER_RANGE;
        int updateVerticalRange = WateringOverlay.DisplayOptions.VERTICAL_RANGE + WATER_RANGE;
        
        BlockPos minPos = originPos.offset(
            -updateRange, -updateVerticalRange, -updateRange
        );
        BlockPos maxPos = originPos.offset(
            updateRange, updateVerticalRange, updateRange
        );
        
        for (int xOffset = minPos.getX(); xOffset <= maxPos.getX(); xOffset++) {
            for (int yOffset = minPos.getY(); yOffset <= maxPos.getY(); yOffset++) {
                for (int zOffset = minPos.getZ(); zOffset <= maxPos.getZ(); zOffset++) {
                    BlockPos blockPos = minPos.offset(xOffset, yOffset, zOffset);
                    BlockState state = level.getBlockState(blockPos);
                    if (state.getBlock() == Blocks.WATER) {
                        putWaterBlock(blockPos);
                    }
                }
            }
        }
    }
    
    private static void putWaterBlock(BlockPos blockPos) {
        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            IMMEDIATE_HYDRATION_BLOCKS.add(blockPos.relative(direction));
        }
    
        for (int xOffset = -WATER_RANGE; xOffset <= WATER_RANGE; xOffset++) {
            for (int zOffset = -WATER_RANGE; zOffset <= WATER_RANGE; zOffset++) {
                FARMLAND_RANGE_BLOCKS.add(blockPos);
            }
        }
    }
    
    public static boolean isInside(BlockPos blockPos) {
        return hasBounds && WATER_BOUNDS.isInside(blockPos);
    }
    
}
