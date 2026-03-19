package com.breakingfemme.block.entity;

import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.texture.Sprite;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.fluid.ModFluids;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class DistillerBlockEntityRenderer implements BlockEntityRenderer<DistillerBlockEntity> {
    public DistillerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(DistillerBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push(); //we need to do this when doing rendering... otherwise the matrices are gonna get fucked after this is rendered
        matrices.translate(0, 0.9375, 0); //a bit outside the box for now, we'll put it inside later! and level depends on block entity

        BlockPos pos = blockEntity.getPos();
        World world = blockEntity.getWorld();
        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayers.getFluidLayer(ModFluids.STILL_BEER.getStill(false)));
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(ModFluids.STILL_SLUDGE);
        Sprite sprite = handler.getFluidSprites(world, pos, ModFluids.STILL_SLUDGE.getDefaultState())[0]; //0 is still (in atlas), 1 is flowing

        float minU = sprite.getMinU();
        float minV = sprite.getMinV();
        float maxU = sprite.getMaxU();
        float maxV = sprite.getMaxV();

        int col = handler.getFluidColor(world, pos, ModFluids.STILL_SLUDGE.getDefaultState());

        Matrix4f pos_matrix = matrices.peek().getPositionMatrix();
        Matrix3f normal_matrix = matrices.peek().getNormalMatrix();
        vc.vertex(pos_matrix, 0, 0, 0)
            .color(col)
            .texture(minU, minV)
            .light(light)
            .overlay(OverlayTexture.DEFAULT_UV)
            .normal(normal_matrix, 0, 0, 0)
            .next();

        vc.vertex(pos_matrix, 0, 0, 1)
            .color(col)
            .texture(minU, maxV)
            .light(light)
            .overlay(OverlayTexture.DEFAULT_UV)
            .normal(normal_matrix, 0, 0, 1)
            .next();

        vc.vertex(pos_matrix, 1, 0, 1)
            .color(col)
            .texture(maxU, maxV)
            .light(light)
            .overlay(OverlayTexture.DEFAULT_UV)
            .normal(normal_matrix, 1, 0, 1)
            .next();

        vc.vertex(pos_matrix, 1, 0, 0)
            .color(col)
            .texture(maxU, minV)
            .light(light)
            .overlay(OverlayTexture.DEFAULT_UV)
            .normal(normal_matrix, 1, 0, 0)
            .next();

        matrices.pop(); //get the old matrices back
    }
}
