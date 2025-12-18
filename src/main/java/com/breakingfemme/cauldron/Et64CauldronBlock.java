package com.breakingfemme.cauldron;

import java.util.Map;

import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
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

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class Et64CauldronBlock extends AbstractCauldronBlock {
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 3;
    public static final IntProperty LEVEL = Properties.LEVEL_3;

    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        CauldronBehavior FILL = (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.fillCauldron(world, pos, player, hand, stack, ModFluids.ET64_CAULDRON.getDefaultState().with(Et64CauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY);
        };

        //vanilla fluids
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ModFluids.ET64_BUCKET, FILL);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ModFluids.ET64_BUCKET, FILL);
        CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ModFluids.ET64_BUCKET, FILL);
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            if(state.get(LEVEL) != 3) //only allow bucketing if cauldron is full
                return ActionResult.PASS;
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.ET64_BUCKET), (statex) -> {
                return true;
            }, SoundEvents.ITEM_BUCKET_FILL);
        });

        //scoop up bottle
        BEHAVIOR.put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(ModItems.ET64_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                decrementFluidLevel(state, world, pos);
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PICKUP, pos);
            }
            return ActionResult.SUCCESS;
        });

        //add bottle to empty cauldron
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ModItems.ET64_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, ModFluids.ET64_CAULDRON.getDefaultState());
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });

        //add Et64 to water cauldron: make Et32 if 1 layer, water if 2 layers
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ModItems.ET64_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (state.get(LEVEL) == 3) {
                return ActionResult.PASS;
            } else if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                if(state.get(LEVEL) == 1)
                    world.setBlockState(pos, ModFluids.ET32_CAULDRON.getDefaultState().with(LEVEL, 2));
                else
                    world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState().with(LEVEL, 3));
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });

        //add Et95 or Et64: just add a layer
        BEHAVIOR.put(ModItems.ET95_BOTTLE, (state, world, pos, player, hand, stack) -> {
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
        BEHAVIOR.put(ModItems.ET64_BOTTLE, (state, world, pos, player, hand, stack) -> {
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

        //add water: make Et32 in all cases
        BEHAVIOR.put(Items.POTION, (state, world, pos, player, hand, stack) -> {
            if (state.get(LEVEL) == 3 || PotionUtil.getPotion(stack) != Potions.WATER) {
                return ActionResult.PASS;
            } else if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                if(state.get(LEVEL) == 1)
                    world.setBlockState(pos, ModFluids.ET32_CAULDRON.getDefaultState().with(LEVEL, 2));
                else
                    world.setBlockState(pos, ModFluids.ET32_CAULDRON.getDefaultState().with(LEVEL, 3));
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });

        //add Et32 or Et64: make Et32
        BEHAVIOR.put(ModItems.ET32_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (state.get(LEVEL) == 3) {
                return ActionResult.PASS;
            } else if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                if(state.get(LEVEL) == 1)
                    world.setBlockState(pos, ModFluids.ET32_CAULDRON.getDefaultState().with(LEVEL, 2));
                else
                    world.setBlockState(pos, ModFluids.ET32_CAULDRON.getDefaultState().with(LEVEL, 3));
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });

        //TODO: dissolve soy inside and leave it macerating (hey thats 70% ethanol! https://www.sciencedirect.com/science/article/pii/S2667010021002511)
    }

    public Et64CauldronBlock(AbstractBlock.Settings settings) {
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
}
