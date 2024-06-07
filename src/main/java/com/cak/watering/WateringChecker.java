package com.cak.watering;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class WateringChecker {
    
    /**Used for tracking farmland hydration*/
    static HashSet<BlockPos> FARMLAND_RANGE_BLOCKS;
    /**Used for tracking sugar cane hydration*/
    static HashSet<BlockPos> IMMEDIATE_HYDRATION_BLOCKS;
    
    public static Level lastLevel = null;
    
    static int LAZY_TICK_INTERVAL = 5;
    static int lazyTick = 0;
    
    public static void tickFarmlandDiscovery(TickEvent.ClientTickEvent event) {
        if (WateringOverlay.DisplayOptions.SELECTOR == OverlaySelector.OFF
            || event.phase == TickEvent.Phase.END)
            return;
        
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        Player player = mc.player;
        if (level == null || player == null)
            return;
    
        lazyTick++;
        if (lazyTick == LAZY_TICK_INTERVAL || lastLevel != level) {
            lazyTick = 0;
        
            lastLevel = level;
            FARMLAND_RANGE_BLOCKS = new HashSet<>();
            IMMEDIATE_HYDRATION_BLOCKS = new HashSet<>();
        
            updateWatering(level, player);
        }
    }
    
    static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    static final int WATER_RANGE = 4;
    
    public static void updateWatering(Level level, Player player) {
        BlockPos originPos = BlockPos.containing(player.position());
        
        int updateRange = WateringOverlay.DisplayOptions.RANGE + WATER_RANGE + 10;
        int updateVerticalRange = WateringOverlay.DisplayOptions.VERTICAL_RANGE + WATER_RANGE;
        
        BlockPos minPos = originPos.offset(
            -updateRange, -updateVerticalRange, -updateRange
        );
        BlockPos maxPos = originPos.offset(
            updateRange, updateVerticalRange, updateRange
        );
        
        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(blockPos);
                    if (state.getBlock() == Blocks.WATER ||
                        state.getFluidState().is(Fluids.WATER) ||
                        state.getFluidState().is(Fluids.FLOWING_WATER)) {
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
                FARMLAND_RANGE_BLOCKS.add(blockPos.offset(xOffset, 0, zOffset));
            }
        }
    }
    
}
