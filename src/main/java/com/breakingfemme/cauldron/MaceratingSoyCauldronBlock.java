package com.breakingfemme.cauldron;

import java.util.Map;

import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class MaceratingSoyCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        //vanilla fluids can replace this if needed
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            if(world.isClient) //if bucketing out, recover the 64% ethanol and the soybeans
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.125f, pos.getZ() + 0.5f, new ItemStack(ModItems.SOYBEANS)));
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.ET64_BUCKET), (statex) -> {
                return true;
            }, SoundEvents.ITEM_BUCKET_FILL);
        });
    }

    public MaceratingSoyCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
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

    protected void onFireCollision(BlockState state, World world, BlockPos pos) { //if on fire, consume fluid from the cauldron, and grab dust
        BlockState blockState = Blocks.CAULDRON.getDefaultState();
        world.setBlockState(pos, blockState);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, Emitter.of(blockState));
        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.125f, pos.getZ() + 0.5f, new ItemStack(ModItems.SOYBEANS)));
    }

    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }

    //TODO: potentially turn into something good after a little while (random ticks, mb IntProperty for maceration start time)
    //optimal parameters for real sterol extraction from soybean: https://www.sciencedirect.com/science/article/pii/S2667010021002511
    //the real process takes about 2 hours, so here it should be 100 seconds, ie 2k ticks
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(random.nextInt(3) != 0) //theres a 2/3 chance of succeeding, which makes the expected value close to 100s at random tick speed 3
            world.setBlockState(pos, Blocks.LAVA_CAULDRON.getDefaultState()); //for now just puts lava in its place (FIXME)
    }
}
