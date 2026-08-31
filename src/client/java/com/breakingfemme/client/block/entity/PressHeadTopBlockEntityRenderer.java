package com.breakingfemme.client.block.entity;

import com.breakingfemme.block.entity.press.PressTopBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class PressHeadTopBlockEntityRenderer implements BlockEntityRenderer<PressTopBlockEntity> {
    private final BlockEntityRendererFactory.Context context;

    public PressHeadTopBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(PressTopBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        try {
            renderRodPosition(entity.getProgress(), entity, matrices, vertexConsumers, light, overlay);
            var head = entity.getHead().orElse(null);
            if (head == null) {
            } // we only render if it matters
            // todo: render head
        } finally {
            matrices.pop();
        }
    }

    private void renderRodPosition(double progress, PressTopBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        try {
            matrices.translate(0f, 0.39 * -progress, 0f);
            context.getRenderManager().getModelRenderer().render(context.getRenderDispatcher().world
                    , context.getRenderManager().getModel(entity.getCachedState()), entity.getCachedState(), entity.getPos(), matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), true, context.getRenderDispatcher().world.random, 20, overlay);
        } finally {
            matrices.pop();
        }
    }
}
