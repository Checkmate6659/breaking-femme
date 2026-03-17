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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class YeastCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        //vanilla fluids can replace this if needed; cannot bucket/bottle out
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);

        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ModItems.YEAST, (state, world, pos, player, hand, stack) -> {
            if (stack.getCount() < 16) { //only able to do this if you have 16 yeast
                return ActionResult.PASS;
            } else if (!world.isClient) {
                Item item = stack.getItem();
                player.getStackInHand(hand).decrement(16); //consume 16 yeast
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, ModFluids.YEAST_CAULDRON.getDefaultState());
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });
    }

    public YeastCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.5625;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if(world.isClient())
            return ActionResult.PASS;

        ItemStack stack = new ItemStack(ModItems.YEAST, 16);
        if (!player.getInventory().insertStack(stack)) {
            player.dropItem(stack, false);
        }
        world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());

        return ActionResult.SUCCESS;
    }
    
    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 1;
    }

    //die (ie turn into empty cauldron) after on average 1/2 mc day i.e. 10 real minutes, i.e. 12k ticks
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(random.nextInt(9) == 0) //every time theres a 1 in 9 chance of dying, to make the expected time about 1 hour
            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
    }

    //in randomDisplayTick, the yeast cauldron should very slowly make bubbles... need a custom particle effect for that tho.
    //or just use the lava texture and be done with it.
}
