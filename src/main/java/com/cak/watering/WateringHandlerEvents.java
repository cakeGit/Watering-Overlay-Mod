package com.cak.watering;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static com.cak.watering.WateringControls.TOGGLE_MODE;
import static com.cak.watering.WateringHighlightRenderer.renderWateringHighlightBox;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class WateringHandlerEvents {
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        WateringChecker.tickFarmlandDiscovery(event);
        WateringControls.tickControls(event);
    }
    
    @SubscribeEvent
    public static void renderLevelLastEvent(RenderLevelStageEvent event) {
        if (WateringOverlay.DisplayOptions.SELECTOR == OverlaySelector.OFF
            || event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER)
            return;
    
        EnumMap<Direction, Boolean> testValues = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values())
            testValues.put(direction, false);
        renderWateringHighlightBox(event, new BlockPos(2, 0, 4), WateredType.FARMLAND.texture, testValues);
        
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
                connectedSides.put(direction, renderedTypeMap.containsKey(blockPos.relative(direction)));
            
            renderWateringHighlightBox(event, blockPos, wateredType.getTexture(), connectedSides);
        }
    }
    
    
}
