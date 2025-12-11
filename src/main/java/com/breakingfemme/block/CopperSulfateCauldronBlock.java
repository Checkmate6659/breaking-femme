package com.breakingfemme.block;

import java.util.Map;

import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class CopperSulfateCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    private static Map<Item, CauldronBehavior> CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    static {
        CauldronBehavior FILL = (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.fillCauldron(world, pos, player, hand, stack, ModFluids.COPPER_SULFATE_CAULDRON.getDefaultState(), SoundEvents.ITEM_BUCKET_EMPTY);
        };

        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ModFluids.COPPER_SULFATE_BUCKET, FILL);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ModFluids.COPPER_SULFATE_BUCKET, FILL);
        CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ModFluids.COPPER_SULFATE_BUCKET, FILL);
        CauldronBehavior.registerBucketBehavior(CAULDRON_BEHAVIOR);
        CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.COPPER_SULFATE_BUCKET), (statex) -> {
                return true;
             }, SoundEvents.ITEM_BUCKET_EMPTY);
       });
    }

    public CopperSulfateCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, CAULDRON_BEHAVIOR);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && entity.isOnFire() && this.isEntityTouchingFluid(state, pos, entity)) {
            entity.extinguish();
            if (entity.canModifyAt(world, pos)) {
                this.onFireCollision(state, world, pos);
            }
        }
    }

    protected void onFireCollision(BlockState state, World world, BlockPos pos) { //if on fire, consume some water from the cauldron, and grab dust
        //BlockState blockState = Blocks.CAULDRON.getDefaultState();
        BlockState blockState = Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 2);
        world.setBlockState(pos, blockState);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, Emitter.of(blockState));
        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.125f, pos.getZ() + 0.5f, new ItemStack(ModItems.COPPER_SULFATE)));
    }

    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }
}
