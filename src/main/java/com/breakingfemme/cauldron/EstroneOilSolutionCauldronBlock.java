package com.breakingfemme.cauldron;

import java.util.Map;
import java.util.Optional;

import com.breakingfemme.datagen.ModItemTagProvider;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class EstroneOilSolutionCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        CauldronBehavior FILL = (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.fillCauldron(world, pos, player, hand, stack, ModFluids.ESTRONE_OIL_SOLUTION_CAULDRON.getDefaultState(), SoundEvents.ITEM_BUCKET_EMPTY);
        };

        //vanilla fluids
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ModFluids.ESTRONE_OIL_SOLUTION_BUCKET, FILL);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ModFluids.ESTRONE_OIL_SOLUTION_BUCKET, FILL);
        CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ModFluids.ESTRONE_OIL_SOLUTION_BUCKET, FILL);
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.ESTRONE_OIL_SOLUTION_BUCKET), (statex) -> {
                return true;
            }, SoundEvents.ITEM_BUCKET_FILL);
        });
    }

    public EstroneOilSolutionCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && entity.isOnFire() && this.isEntityTouchingFluid(state, pos, entity)) {
            entity.setOnFireFromLava(); //this stuff is FLAMMABLE!!
        }
    }

    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }

    //in the real world, crude estrone would be recovered by filtration
    //we're gonna be using a cauldron - hopper - cauldron setup
    //automating that in vanilla would be quite a mess i imagine, need like chest/hopper minecarts or sth, and a piston system
    //cuz yes, the hopper would have to point down.
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos hopper_pos = pos.down();
        BlockPos low_cauldron_pos = hopper_pos.down();
        BlockState low_cauldron_state = world.getBlockState(low_cauldron_pos);
        if(!(low_cauldron_state.isOf(Blocks.CAULDRON))) //need an empty cauldron at the bottom
            return; //the reception cauldron spot is NOT a cauldron
        if(!world.getBlockState(hopper_pos).equals(Blocks.HOPPER.getDefaultState())) //default state is downwards facing hopper
            return; //not an unlocked downwards facing hopper => not good

        Optional<HopperBlockEntity> hopperq = world.getBlockEntity(hopper_pos, BlockEntityType.HOPPER);
        if(hopperq.isEmpty()) return;
        HopperBlockEntity hopper = hopperq.get();

        //place estrone in non-full slot which already has estrone, or in a new empty slot
        int idx_empty = -1;
        int idx_filter = -1;
        int idx_estrone = -1;
        for(int i = 0; i < 5; i++)
        {
            ItemStack stk = hopper.getStack(i);
            if(idx_empty == -1 && stk.isEmpty())
                idx_empty = i;
            else if(idx_estrone == -1 && stk.getCount() < 64 && stk.isOf(ModItems.CRUDE_ESTRONE))
                idx_estrone = i;
            else if(idx_filter == -1 && stk.isIn(ModItemTagProvider.FILTER)) //flimsy filters are fine here
                idx_filter = i;
        }
        if(idx_estrone == -1) idx_estrone = idx_empty;
        if(idx_estrone == -1 || idx_filter == -1) return; //no slot to consume filter from or deposit estrone onto

        //filter estrone out of the solution: use up or damage filter, replace with crude estrone
        ItemStack filter = hopper.getStack(idx_filter);
        if(filter.isDamageable()) //does filter have durability
            filter.damage(1, world.random, null); //just use one bit of durability
        else
            filter.decrement(1); //consume a piece of filter

        if(idx_empty != idx_estrone)
            hopper.getStack(idx_estrone).increment(1); //just add one bit of estrone
        else
            hopper.setStack(idx_estrone, new ItemStack(ModItems.CRUDE_ESTRONE)); //new slot!

        //replace block states
        world.setBlockState(low_cauldron_pos, ModFluids.ANDROSTADIENEDIONE_OIL_SOLUTION_CAULDRON.getDefaultState().with(AndrostadienedioneOilSolutionCauldronBlock.LEVEL, 3));
        world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
    }
}
