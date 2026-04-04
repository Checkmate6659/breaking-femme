package com.breakingfemme.block.entity;

import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class DistillerBlockEntityRenderer implements BlockEntityRenderer<DistillerBlockEntity> {
    public DistillerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(DistillerBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Pair<FluidVariant, Integer> fluid_pair = blockEntity.getFluid(0);
        FluidVariant fluidv = fluid_pair.getLeft();
        Fluid fluid = fluidv.getObject();
        if(!(fluid instanceof FlowableFluid)) return; //TODO: be able to render non-flowable fluids too (look at create mod ig, with sth like milk)
        FlowableFluid ffluid = (FlowableFluid)fluid;
        int level = fluid_pair.getRight();

        if(level == 0) //empty => do nothing special
            return;

        matrices.push(); //we need to do this when doing rendering... otherwise the matrices are gonna get fucked after this is rendered
        double height = 0.125 + level * 1.0030864197530864e-05; //top out at 0.9375 when full (at 81000)
        matrices.translate(0, height, 0); //a bit outside the box for now, we'll put it inside later! and level depends on block entity

        BlockPos pos = blockEntity.getPos();
        World world = blockEntity.getWorld();
        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayers.getFluidLayer(ffluid.getStill(false)));
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(ffluid);
        Sprite sprite = handler.getFluidSprites(world, pos, ffluid.getDefaultState())[0]; //0 is still (in atlas), 1 is flowing

        float minU = sprite.getMinU();
        float minV = sprite.getMinV();
        float maxU = sprite.getMaxU();
        float maxV = sprite.getMaxV();

        int col = handler.getFluidColor(world, pos, ffluid.getDefaultState());
        if(col < 0x1000000) col |= 0xFF000000; //if fluid would be completely invisible (alpha = 0), set its alpha to 1

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
