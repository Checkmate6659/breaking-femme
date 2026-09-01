package com.breakingfemme.client.block.entity;

import com.breakingfemme.block.entity.press.PressTopBlockEntity;
import com.breakingfemme.registries.press.PressHead;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.breakingfemme.BreakingFemme.id;

@Environment(EnvType.CLIENT)
public class PressHeadTopBlockEntityRenderer implements BlockEntityRenderer<PressTopBlockEntity> {
    private final BlockEntityRendererFactory.Context context;
    private final Identifier EMPTY_MODEL = id("block/press/empty");
    public PressHeadTopBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(PressTopBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        try {
            matrices.translate(0f, 0.39 * -entity.getProgress(), 0f);
            renderRodPosition(entity, matrices, vertexConsumers, light, overlay);
            var head = entity.getHead().orElse(null);
            if (head != null) {
                renderHeadPosition(head, matrices, vertexConsumers, light, overlay, entity.getPos());
            }
        } finally {
            matrices.pop();
        }
    }

    private void renderHeadPosition(PressHead head, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BlockPos pos) {
        var id = head.getVariantFile();
        matrices.push();
        try {
            var model = context.getRenderManager().getModels().getModelManager().getModel(id);

            context.getRenderManager().getModelRenderer().render(context.getRenderDispatcher().world, context.getRenderManager().getModels().getModelManager().getModel(id), null, pos, matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), false, context.getRenderDispatcher().world.random, 20, overlay);
        } finally {
            matrices.pop();
        }
    }

    private void renderRodPosition(PressTopBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        try {
            context.getRenderManager().getModelRenderer().render(context.getRenderDispatcher().world
                    , context.getRenderManager().getModel(entity.getCachedState()), entity.getCachedState(), entity.getPos(), matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), true, context.getRenderDispatcher().world.random, 20, overlay);
        } finally {
            matrices.pop();
        }
    }
}
