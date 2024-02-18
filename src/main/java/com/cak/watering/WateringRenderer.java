package com.cak.watering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class WateringRenderer {
    
    
    //Corners can clip without issue, lucky me!
    static final float PATTERN_ORIGIN = 16/256f;
    
    static final ResourceLocation OVERLAY_LOCATION = WateringOverlay.asResource("textures/water_overlay.png");
    
    @SubscribeEvent
    public static void renderLevelLastEvent(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;
        renderBox(event, new BlockPos(2, 0, 2), true);
    
        Level level = event.getCamera().getEntity().level();
        if (level != WateringChecker.lastLevel)
            return;
        
        //WateringChecker.FARMLAND_RANGE_BLOCKS will safely include WateringChecker.IMMEDIATE_RANGE_BLOCKS
        
        for (BlockPos blockPos : WateringChecker.FARMLAND_RANGE_BLOCKS) {
            BlockState state = level.getBlockState(blockPos);
            
            //Check to use blue or green texture
            boolean isFarmlandRendering = WateringOverlay.DisplayOptions.SELECTOR.shouldRenderInFarmlandRange(state);
            
            boolean isRendering = isFarmlandRendering ||
                (WateringChecker.IMMEDIATE_HYDRATION_BLOCKS.contains(blockPos)
                    && WateringOverlay.DisplayOptions.SELECTOR.shouldRenderInImmediateRange(state));
            
            if (isRendering)
                renderBox(event, blockPos, isFarmlandRendering);
        }
    }
    
    private static void renderBox(RenderLevelStageEvent event, BlockPos blockPos, boolean isFarmlandRendering) {;
        
        PoseStack poseStack = event.getPoseStack();

        
        for (Direction direction : Direction.values()) {
            poseStack.pushPose();
    
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    
            Matrix4f m = poseStack.last().pose();
            
            buffer.vertex(m, -8, 8, 0)
                .uv(0, 0).endVertex();
            buffer.vertex(m, -8, -8, 0)
                .uv(0, 1).endVertex();
            buffer.vertex(m, 8, -8, 0)
                .uv(1, 0).endVertex();
            buffer.vertex(m, 8, 8, 0)
                .uv(1, 1).endVertex();
    
            tesselator.end();
            poseStack.popPose();
        }
    
    }
    
}
