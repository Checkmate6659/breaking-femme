package com.breakingfemme.cauldron;

import java.util.Map;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class AndrostadienedioneCauldronBlock extends AbstractCauldronBlock {
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 3;
    public static final IntProperty LEVEL = Properties.LEVEL_3;

    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        CauldronBehavior FILL = (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.fillCauldron(world, pos, player, hand, stack, ModFluids.ANDROSTADIENEDIONE_CAULDRON.getDefaultState().with(LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY);
        };

        //vanilla fluids
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ModFluids.ANDROSTADIENEDIONE_BUCKET, FILL);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ModFluids.ANDROSTADIENEDIONE_BUCKET, FILL);
        CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ModFluids.ANDROSTADIENEDIONE_BUCKET, FILL);
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.ANDROSTADIENEDIONE_BUCKET), (statex) -> {
                return true;
            }, SoundEvents.ITEM_BUCKET_FILL);
        });

        //scoop up bottle
        BEHAVIOR.put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(ModItems.ANDROSTADIENEDIONE_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                decrementFluidLevel(state, world, pos);
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PICKUP, pos);
            }
            return ActionResult.SUCCESS;
        });

        //add bottle to empty cauldron
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ModItems.ANDROSTADIENEDIONE_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, ModFluids.ANDROSTADIENEDIONE_CAULDRON.getDefaultState());
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });

        //add bottle to partially full cauldron
        BEHAVIOR.put(ModItems.ANDROSTADIENEDIONE_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (state.get(LEVEL) == 3) {
                return ActionResult.PASS;
            } else if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, (BlockState)state.cycle(LEVEL));
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });
    }

    public AndrostadienedioneCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
        this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 1));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    public boolean isFull(BlockState state) {
        return (Integer)state.get(LEVEL) == 3;
    }

    public static void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
        LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
    }

    protected double getFluidHeight(BlockState state) {
        return (6.0 + (double)(Integer)state.get(LEVEL) * 3.0) / 16.0;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(LEVEL);
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && this.isEntityTouchingFluid(state, pos, entity)) {
            if(entity.isLiving())
                ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 2));
            if(entity.isPlayer()) {
                entity.damage(new DamageSource( //https://en.wikipedia.org/wiki/Novikov_self-consistency_principle
                    world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BreakingFemme.NOVIKOV)), 37921489645.0f
                );
            }
            else if (entity.isLiving())
                ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 2));
        }
    }
}
