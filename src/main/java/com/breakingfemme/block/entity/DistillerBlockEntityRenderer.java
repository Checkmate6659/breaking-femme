package com.breakingfemme.block.entity;

import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;

import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class DistillerBlockEntityRenderer implements BlockEntityRenderer<DistillerBlockEntity> {
    //check out VertexConsumer.java: these ints are read as bytes (all little endian) and interpreted as:
    //x of vertex: float (4 bytes)
    //y of vertex: float (4 bytes)
    //z of vertex: float (4 bytes)
    //r, g and b values (1 byte each)
    //1 unused byte
    //u of vertex: float (4 bytes)
    //v of vertex: float (4 bytes)
    //8 unused bytes
    //for a total of 32 bytes, or 8 ints
    //here we set all vertex y values to be 0, to control the fluid level easier
    private static int[] VERTEX_DATA = {
        0x3E000000, 0, 0x3E000000, -1, 0x3E000000, 0x3E000000, 42, 42,
        0x3E000000, 0, 0x3F600000, -1, 0x3E000000, 0x3F600000, 42, 42,
        0x3F600000, 0, 0x3F600000, -1, 0x3F600000, 0x3F600000, 42, 42,
        0x3F600000, 0, 0x3E000000, -1, 0x3F600000, 0x3E000000, 42, 42
    };

    // A jukebox itemstack
    private static final ItemStack stack = new ItemStack(ModItems.CHILI_PEPPER);
    
    public DistillerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(DistillerBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push(); //we need to do this when doing rendering... otherwise the matrices are gonna get fucked after this is rendered

        // Calculate the current offset in the y value
        double offset = Math.sin((blockEntity.getWorld().getTime() + tickDelta) / 8.0) / 4.0;
        // Move the item
        matrices.translate(0.5, 1.25 + offset, 0.5);
 
        // Rotate the item
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((blockEntity.getWorld().getTime() + tickDelta) * 4));

        //render the item
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);

        matrices.pop(); //get the old matrices back

        //end of test section for checking if the rendering works
        //TODO: remove code before when done!

        matrices.push(); //we need to do this when doing rendering... otherwise the matrices are gonna get fucked after this is rendered
        matrices.translate(0, 2.0, 0); //a bit outside the box for now, we'll put it inside later!

        BlockPos pos = blockEntity.getPos();
        World world = blockEntity.getWorld();
        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayers.getFluidLayer(ModFluids.STILL_BEER.getStill(false)));
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(ModFluids.STILL_SLUDGE);
        //Sprite sprite = handler.getFluidSprites(world, pos, ModFluids.STILL_SLUDGE.getDefaultState())[0]; //0 is still (in atlas), 1 is flowing
        Sprite sprite = FluidVariantRendering.getSprite(FluidVariant.of(ModFluids.STILL_COAL_OIL));

        //we need to recompute the vertex data here, in order not to get the entire atlas but just the texture we want
        float delta = (sprite.getFrameU(16.0) - sprite.getFrameU(0.0)) * 0.125f; //to not have a tiny bit of fluid poking out on the side
        float mu = sprite.getFrameU(0.0) + delta;
        float mv = sprite.getFrameV(0.0) + delta;
        float Mu = mu + 1 - delta - delta;
        float Mv = mv + 1 - delta - delta;
        VERTEX_DATA[8 * 0 + 4] = Float.floatToRawIntBits(mu);
        VERTEX_DATA[8 * 0 + 5] = Float.floatToRawIntBits(mv);
        VERTEX_DATA[8 * 1 + 4] = Float.floatToRawIntBits(mu);
        VERTEX_DATA[8 * 1 + 5] = Float.floatToRawIntBits(Mv);
        VERTEX_DATA[8 * 2 + 4] = Float.floatToRawIntBits(Mu);
        VERTEX_DATA[8 * 2 + 5] = Float.floatToRawIntBits(Mv);
        VERTEX_DATA[8 * 3 + 4] = Float.floatToRawIntBits(Mu);
        VERTEX_DATA[8 * 3 + 5] = Float.floatToRawIntBits(mv);

        BakedQuad quad = new BakedQuad(VERTEX_DATA, 0, Direction.UP, sprite, false);
        int col = handler.getFluidColor(world, pos, ModFluids.STILL_SLUDGE.getDefaultState());
        vc.quad(matrices.peek(), quad, ColorHelper.Argb.getRed(col) * 0.00390625f, ColorHelper.Argb.getGreen(col) * 0.00390625f, ColorHelper.Argb.getBlue(col) * 0.00390625f, light, overlay);

        //handler.getFluidSprites(world, pos, ModFluids.STILL_SLUDGE.getDefaultState())[0].getMinU().
        
        matrices.pop(); //get the old matrices back
    }
}
