package com.breakingfemme.block;

import com.breakingfemme.ModFluids;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class KelpAshBlock extends FallingBlock {
    public KelpAshBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return 5131854; //dark gray
    }
  
    //click on with water bottle => get the ashes wet (turn it into a fluid)
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        //basically copied over from potion code for mud conversion
        ItemStack stack = player.getStackInHand(hand);
        if(stack.isOf(Items.POTION) && PotionUtil.getPotion(stack) == Potions.WATER)
        {
            world.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld)world;

                for(int i = 0; i < 5; ++i) {
                serverWorld.spawnParticles(ParticleTypes.SPLASH, (double)pos.getX() + world.random.nextDouble(), (double)(pos.getY() + 1), (double)pos.getZ() + world.random.nextDouble(), 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)1.0F);
                }
            }

            world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            world.setBlockState(pos, ModFluids.STILL_KELP_ASH_MUD.getDefaultState().getBlockState());
            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    }
}
