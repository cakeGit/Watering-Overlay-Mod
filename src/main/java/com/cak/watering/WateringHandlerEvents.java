package com.cak.watering;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static com.cak.watering.WateringHighlightRenderer.renderWateringHighlightBox;

@EventBusSubscriber(Dist.CLIENT)
public class WateringHandlerEvents {
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        WateringChecker.tickFarmlandDiscovery();
        WateringControls.tickControls();
    }
    
    @SubscribeEvent
    public static void renderLevelLastEvent(RenderLevelStageEvent event) {
        if (WateringOverlay.DisplayOptions.SELECTOR == OverlaySelector.OFF
            || event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER)
            return;
        
        Level level = event.getCamera().getEntity().level();
        if (level != WateringChecker.lastLevel)
            return;
        
        //WateringChecker.FARMLAND_RANGE_BLOCKS will safely include WateringChecker.IMMEDIATE_RANGE_BLOCKS
    
        //Build rendering data for connected textures
        Map<BlockPos, WateredType> renderedTypeMap = new HashMap<>();
        
        for (BlockPos blockPos : WateringChecker.FARMLAND_RANGE_BLOCKS) {
            BlockState state = level.getBlockState(blockPos);
            
            if (WateringOverlay.DisplayOptions.SELECTOR.shouldRenderInFarmlandRange(state))
                renderedTypeMap.put(blockPos, WateredType.FARMLAND);
            
            else if (WateringChecker.IMMEDIATE_HYDRATION_BLOCKS.contains(blockPos)
                && WateringOverlay.DisplayOptions.SELECTOR.shouldRenderInSugarCaneRange(state))
                renderedTypeMap.put(blockPos, WateredType.SUGAR_CANE_ONLY);
        }
        
        //Rendering oo la la
        for (Map.Entry<BlockPos, WateredType> entry : renderedTypeMap.entrySet()) {
            BlockPos blockPos = entry.getKey();
            WateredType wateredType = entry.getValue();
            
            EnumMap<Direction, Boolean> connectedSides = new EnumMap<>(Direction.class);
            for (Direction direction : Direction.values())
                connectedSides.put(direction, renderedTypeMap.containsKey(blockPos.relative(direction.getOpposite())));
            
            renderWateringHighlightBox(event, blockPos, wateredType.getTexture(), connectedSides);
        }
    }
    
    
}
