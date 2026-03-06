package com.breakingfemme.cauldron;

import java.util.Map;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class AndrostadienedioneExtractionCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    public static final IntProperty STIRS = IntProperty.of("stirs", 0, 3);

    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        //vanilla fluids can replace this if needed
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            if(world.isClient) //if bucketing out, recover the copper sulfate and the pulverized nickel
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.125f, pos.getZ() + 0.5f, new ItemStack(ModItems.PULVERIZED_NICKEL)));
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.COPPER_SULFATE_BUCKET), (statex) -> {
                return true;
            }, SoundEvents.ITEM_BUCKET_FILL);
        });

        //mix it around with a stick (kind of an issue that we cant use a tag; i could have added tag containing stick, end rod, shovels and metal pipes)
        BEHAVIOR.put(Items.STICK, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                if(state.get(STIRS) < 3) //if already fully stirred, don't keep going; more mixing won't hurt
                    world.setBlockState(pos, state.cycle(STIRS));
                world.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);

                for(int i = 0; i < world.random.nextInt(1) + 1; ++i) { //add little bubbles while mixing
                    ((ServerWorld)world).spawnParticles(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5, pos.getY() + 0.9375, pos.getZ() + 0.5, 3, 0.25, 0, 0.25, 0);
                }
            }
            return ActionResult.success(world.isClient);
        });

        //scoop up the oil from the top (the rest is trash)
        BEHAVIOR.put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(state.get(STIRS) == 3 ? ModItems.ANDROSTADIENEDIONE_OIL_BOTTLE : ModItems.COAL_OIL_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, ModFluids.SLUDGE_CAULDRON.getDefaultState()); //who knows what's in there? (it is kinda weird that its not level 2; well sludge isnt leveled) (we could empty out the cauldron... but too easy to automate)
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PICKUP, pos);
            }
            return ActionResult.success(world.isClient);
        });
    }

    public AndrostadienedioneExtractionCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
        this.setDefaultState(this.stateManager.getDefaultState().with(STIRS, 0));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STIRS);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.9375;
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

    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }
}
