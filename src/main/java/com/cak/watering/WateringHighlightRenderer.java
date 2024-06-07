package com.cak.watering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.EnumMap;

public class WateringHighlightRenderer {
    
    public static void renderWateringHighlightBox(RenderLevelStageEvent event, BlockPos blockPos, ResourceLocation texture, EnumMap<Direction, Boolean> connectedSides) {
        PoseStack poseStack = event.getPoseStack();
        
        RenderSystem.setShaderColor(1, 1, 1, 0.5f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
    
        poseStack.pushPose();
        
        AABB renderedCubeAABB = new AABB(
            new BlockPos(0, 0, 0),
            new BlockPos(1, 1, 1)
        );
        
        for (Direction direction : Direction.values()) {
            if (!connectedSides.get(direction))
                renderedCubeAABB = includeAABBs(renderedCubeAABB, cubeOnSide(direction.getOpposite().getNormal(), 1/16f, 1f));
        }
        
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
    
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        
        poseStack.mulPoseMatrix(new Matrix4f().rotate(camera.rotation()));
        translatePSVector(poseStack, camera.getPosition().multiply(1, -1, 1));
        translatePSVector(poseStack, Vec3.atLowerCornerOf(blockPos.offset(1, 0, 1)).multiply(-1, 1, -1));
        
        renderHighlightCube(poseStack, buffer, renderedCubeAABB, connectedSides);
    
        tesselator.end();
        poseStack.popPose();
        
        RenderSystem.setShaderColor(1, 1, 1, 1f);
        
    }
    
    private static void translatePSVector(PoseStack poseStack, Vec3 vec) {
        poseStack.translate(vec.x(), vec.y(), vec.z());
    }
    
    private static void renderHighlightCube(PoseStack poseStack, BufferBuilder buffer, AABB renderedCubeAABB, EnumMap<Direction, Boolean> connectedSides) {
        if (!connectedSides.get(Direction.NORTH))
            renderHighlightCubeSide(poseStack, buffer, Direction.NORTH, renderedCubeAABB,
                new Vec3(0, 0, 1),
                new Vec3(1, 1, 1)
            );
        if (!connectedSides.get(Direction.SOUTH))
            renderHighlightCubeSide(poseStack, buffer, Direction.SOUTH, renderedCubeAABB,
                new Vec3(0, 0, 0),
                new Vec3(1, 1, 0)
            );
        if (!connectedSides.get(Direction.WEST))
            renderHighlightCubeSide(poseStack, buffer, Direction.WEST, renderedCubeAABB,
                new Vec3(1, 0, 0),
                new Vec3(1, 1, 1)
            );
        if (!connectedSides.get(Direction.EAST))
            renderHighlightCubeSide(poseStack, buffer, Direction.EAST, renderedCubeAABB,
                new Vec3(0, 0, 0),
                new Vec3(0, 1, 1)
            );
        if (!connectedSides.get(Direction.UP))
            renderHighlightCubeSide(poseStack, buffer, Direction.UP, renderedCubeAABB,
                new Vec3(0, 1, 0),
                new Vec3(1, 1, 1)
            );
        if (!connectedSides.get(Direction.DOWN))
            renderHighlightCubeSide(poseStack, buffer, Direction.DOWN, renderedCubeAABB,
                new Vec3(0, 0, 0),
                new Vec3(1, 0, 1)
            );
    }
    
    
    private static void renderHighlightCubeSide(PoseStack poseStack, BufferBuilder buffer, Direction direction, AABB renderedCubeAABB, Vec3 from, Vec3 to) {
        Vec3 min = minVector(renderedCubeAABB);
        Vec3 max = maxVector(renderedCubeAABB);
        
        Vec3 diff = max.subtract(min);
        
        from = from.multiply(diff).add(min);
        to = to.multiply(diff).add(min);
        
        Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal());
        double normalOrdinal = sumVector(normal);
        Vec3 perpendiculars = new Vec3(1, 1, 1).subtract(normal.scale(normalOrdinal));
        
        Vec3 firstAxis = getFirstAxis(perpendiculars);
        Vec3 firstTo = from.add(diff.multiply(firstAxis));
        
        Vec3 secondaryAxis = getSecondaryAxis(perpendiculars);
        Vec3 secondaryTo = from.add(diff.multiply(secondaryAxis));
        
        float PATTERN_BORDER = 4/256f;
        float PATTERN_WIDTH = 4/256f;
        
        Vec3 realDiff = to.subtract(from);
        Vec3 realDiffPrimary = realDiff.multiply(getFirstAxis(realDiff));
        Vec3 realDiffSecondary = realDiff.multiply(getSecondaryAxis(realDiff));

        float primaryUV = (float) ((sumVector(realDiffPrimary) / (1/16f)) * (4/256f));
        float secondaryUV = (float) ((sumVector(realDiffSecondary) / (1/16f)) * (4/256f));
        
        Matrix4f m = poseStack.last().pose();
        
        if (normalOrdinal == 1)
            buffer.vertex(m, (float) to.x, (float) to.y, (float) to.z)
                .uv2(0, 0)
                .uv(primaryUV, secondaryUV).endVertex();
        else
            buffer.vertex(m, (float) from.x, (float) from.y, (float) from.z)
                .uv2(0, 0)
                .uv(0, 0).endVertex();
        
        buffer.vertex(m, (float) firstTo.x, (float) firstTo.y, (float) firstTo.z)
            .uv2(0, 0)
            .uv(0, secondaryUV).endVertex();
        
        if (!(normalOrdinal == 1))
            buffer.vertex(m, (float) to.x, (float) to.y, (float) to.z)
                .uv2(0, 0)
                .uv(primaryUV, secondaryUV).endVertex();
        else
            buffer.vertex(m, (float) from.x, (float) from.y, (float) from.z)
                .uv2(0, 0)
                .uv(0, 0).endVertex();
        
        buffer.vertex(m, (float) secondaryTo.x, (float) secondaryTo.y, (float) secondaryTo.z)
            .uv2(0, 0)
            .uv(primaryUV, 0).endVertex();
    
    }
    
    private static Vec3 getFirstAxis(Vec3 perpendiculars) {
        return perpendiculars.x != 0 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
    }
    
    private static Vec3 getSecondaryAxis(Vec3 perpendiculars) {
        return perpendiculars.z != 0 ? new Vec3(0, 0, 1) : new Vec3(0, 1, 0);
    }
    
    private static AABB cubeOnSide(Vec3i normalVec3i, float height, float width) {
        Vec3 normal = Vec3.atLowerCornerOf(normalVec3i);
        normal = new Vec3(normal.x, -normal.y, normal.z);
        
        double normalOrdinal = sumVector(normal); //Expect axis aligned so no need for anything else
        
        Vec3 faceCenter = new Vec3(0.5, 0.5, 0.5).add(normal.scale(0.5));
        
        Vec3 edgeOffset = new Vec3(1, 1, 1).subtract(normal.scale(normalOrdinal))
            .scale(0.5).scale(width);
        
        Vec3 min = faceCenter.subtract(edgeOffset);
        Vec3 max = faceCenter.add(edgeOffset).add(normal.scale(height));
        
        return new AABB(min, max);
    }
    
    private static double sumVector(Vec3 normal) {
        return normal.x() + normal.y() + normal.z();
    }
    
    private static Vec3 minVector(AABB AABB) {
        return new Vec3(AABB.minX, AABB.minY, AABB.minZ);
    }
    
    private static Vec3 maxVector(AABB AABB) {
        return new Vec3(AABB.maxX, AABB.maxY, AABB.maxZ);
    }
    
    private static AABB includeAABBs(AABB from, AABB to) {
        return new AABB(
            Math.min(from.minX, to.minX),
            Math.min(from.minY, to.minY),
            Math.min(from.minZ, to.minZ),
            Math.max(from.maxX, to.maxX),
            Math.max(from.maxY, to.maxY),
            Math.max(from.maxZ, to.maxZ)
        );
    }
    
}
