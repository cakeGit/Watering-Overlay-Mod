package com.cak.watering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class WateringRenderer {
    
    
    //Corners can clip without issue, lucky me!
    static final float PATTERN_WIDTH = 16 / 256f;
    
    static final ResourceLocation OVERLAY_LOCATION = WateringOverlay.asResource("textures/water_overlay.png");
    
    @SubscribeEvent
    public static void renderLevelLastEvent(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;
        renderBox(event, new BlockPos(2, 0, 2), true);
        renderBox(event, new BlockPos(1, 0, 2), true);
        
        Level level = event.getCamera().getEntity().level();
        if (level != WateringChecker.lastLevel)
            return;
        
        //WateringChecker.FARMLAND_RANGE_BLOCKS will safely include WateringChecker.IMMEDIATE_RANGE_BLOCKS

//        for (BlockPos blockPos : WateringChecker.FARMLAND_RANGE_BLOCKS) {
//            BlockState state = level.getBlockState(blockPos);
//
//            //Check to use blue or green texture
//            boolean isFarmlandRendering = WateringOverlay.DisplayOptions.SELECTOR.shouldRenderInFarmlandRange(state);
//
//            boolean isRendering = isFarmlandRendering ||
//                (WateringChecker.IMMEDIATE_HYDRATION_BLOCKS.contains(blockPos)
//                    && WateringOverlay.DisplayOptions.SELECTOR.shouldRenderInImmediateRange(state));
//
//            if (isRendering)
//                renderBox(event, blockPos, isFarmlandRendering);
//        }
    }
    
    private static void renderBox(RenderLevelStageEvent event, BlockPos blockPos, boolean isFarmlandRendering) {
        ;
        
        PoseStack poseStack = event.getPoseStack();
        
        
        for (Direction direction : Direction.values()) {
            poseStack.pushPose();
            
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WateringOverlay.asResource("textures/water_overlay.png"));
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            
            Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            poseStack.translate((float) -camera.x, (float) -camera.y, (float) -camera.z);
    
            poseStack.pushPose();
            poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            poseStack.scale(18/16f, 18/16f, 18/16f);
            poseStack.translate(-1/16f, -1/16f, -1/16f);
            renderFace(poseStack, buffer, direction);
            poseStack.popPose();
            
            tesselator.end();
            poseStack.popPose();
        }
        
    }
    
    public static void renderFace(PoseStack poseStack, BufferBuilder buffer, Direction direction) {
        
        poseStack.translate(0.5, 0.5, 0.5);
        
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.get2DDataValue() == -1 ? 0 : direction.get2DDataValue() * 90));
        poseStack.mulPose(Axis.XP.rotationDegrees(direction.getAxis() == Direction.Axis.Y ? direction == Direction.UP ? 90 : -90 : 0));
        poseStack.translate(0, 0, 0.5);
        
        
        renderFace(poseStack, buffer,
            new Vec3(0.5, -0.5, 0),
            new Vec3(0.5, 0.5, 0),
            new Vec3(-0.5, 0.5, 0),
            new Vec3(-0.5, -0.5, 0)
        );
    }
    
    public static void renderFace(PoseStack poseStack, BufferBuilder buffer, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
        float PATTERN_BORDER = 1/256f;
        
        float realPATTERN_BORDER = PATTERN_BORDER * 4;
        float realPATTERN_WIDTH = PATTERN_WIDTH * 4;
        
        float uvMin = realPATTERN_WIDTH - realPATTERN_BORDER;
        float uvMax = (realPATTERN_WIDTH*2) + realPATTERN_BORDER;
        
        Matrix4f m = poseStack.last().pose();
        buffer.vertex(m, (float) v1.x, (float) v1.y, (float) v1.z)
            .uv2(0, 0)
            .uv(uvMin, uvMin).endVertex();
        buffer.vertex(m, (float) v2.x, (float) v2.y, (float) v2.z)
            .uv2(0, 0)
            .uv(uvMin, uvMax).endVertex();
        buffer.vertex(m, (float) v3.x, (float) v3.y, (float) v3.z)
            .uv2(0, 0)
            .uv(uvMax, uvMax).endVertex();
        buffer.vertex(m, (float) v4.x, (float) v4.y, (float) v4.z)
            .uv2(0, 0)
            .uv(uvMax, uvMin).endVertex();
    }
    
}
