package com.breakingfemme.cauldron;

import java.util.Map;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.ThermalUtil;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
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
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class EstroneReductionCauldronBlock extends AbstractCauldronBlock {
    public static final BooleanProperty HAS_ESTRONE = BooleanProperty.of("has_estrone");
    public static final BooleanProperty HAS_RNICKEL = BooleanProperty.of("has_raney_nickel");
    public static final BooleanProperty IS_DONE = BooleanProperty.of("done");

    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        //vanilla fluids can replace this if needed
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        
        //final estradiol precipitation using hydrochloric acid
        BEHAVIOR.put(ModFluids.HYDROCHLORIC_ACID_BUCKET, (state, world, pos, player, hand, stack) -> {
            if(!state.get(IS_DONE))
                return ActionResult.PASS;
            if(!world.isClient)
            {
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.125f, pos.getZ() + 0.5f, new ItemStack(ModItems.PURE_ESTRADIOL_POWDER))); //FIXME: crude estradiol! then recrystallize
                world.setBlockState(pos, ModFluids.SLUDGE_CAULDRON.getDefaultState());
                world.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });

        //adding pure estrone (if the cauldron doesn't already have estrone)
        BEHAVIOR.put(ModItems.PURE_ESTRONE, (state, world, pos, player, hand, stack) -> {
            if(state.get(HAS_ESTRONE) || state.get(IS_DONE))
                return ActionResult.PASS;
            if(!world.isClient)
            {
                player.getStackInHand(hand).decrement(1);
                world.setBlockState(pos, state.with(HAS_ESTRONE, true));
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });

        //same with raney nickel
        BEHAVIOR.put(ModItems.RANEY_NICKEL, (state, world, pos, player, hand, stack) -> {
            if(state.get(HAS_RNICKEL) || state.get(IS_DONE))
                return ActionResult.PASS;
            if(!world.isClient)
            {
                player.getStackInHand(hand).decrement(1);
                world.setBlockState(pos, state.with(HAS_RNICKEL, true));
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });

        //we need to add both these methods for the concentrated caustic soda cauldron too
        //ideally it would be ethanol + caustic soda, may need to impl that later with the (false, false) state
        ConcentratedCausticSodaCauldronBlock.BEHAVIOR.put(ModItems.PURE_ESTRONE, (state, world, pos, player, hand, stack) -> {
            if(!world.isClient)
            {
                player.getStackInHand(hand).decrement(1);
                world.setBlockState(pos, ModFluids.ESTRONE_REDUCTION_CAULDRON.getDefaultState().with(HAS_ESTRONE, true));
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });
        ConcentratedCausticSodaCauldronBlock.BEHAVIOR.put(ModItems.RANEY_NICKEL, (state, world, pos, player, hand, stack) -> {
            if(!world.isClient)
            {
                player.getStackInHand(hand).decrement(1);
                world.setBlockState(pos, ModFluids.ESTRONE_REDUCTION_CAULDRON.getDefaultState().with(HAS_RNICKEL, true));
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });
    }

    public EstroneReductionCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
        this.setDefaultState(this.stateManager.getDefaultState().with(HAS_ESTRONE, false).with(HAS_RNICKEL, false).with(IS_DONE, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_ESTRONE, HAS_RNICKEL, IS_DONE);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && this.isEntityTouchingFluid(state, pos, entity)) {
            //caustic fluid => damage entity
            entity.damage(new DamageSource(
                world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BreakingFemme.CORROSION)), 9.5f
            );
        }
    }

    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }

    //turn into "done" state if you cool it and let it sit for 2 mc hours ie have a 2/3 chance of triggering on a random tick
    //and turn into sludge if you heat it
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //failure condition
        if(world.getDimension().ultrawarm() || ThermalUtil.isBlockHot(world, pos.down()))
            world.setBlockState(pos, ModFluids.SLUDGE_CAULDRON.getDefaultState());

        //success condition
        if(random.nextInt(3) != 0 && ThermalUtil.isCauldronCold(world, pos))
            world.setBlockState(pos, ModFluids.ESTRONE_REDUCTION_CAULDRON.getDefaultState().with(IS_DONE, true));
    }

    //doing boiling effect when its hot, and TODO: condensation (dripping) effect when its cold
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.getDimension().ultrawarm() || ThermalUtil.isBlockHot(world, pos.down())) {
            if(random.nextInt(3) == 0)
                world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.75, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 192F + random.nextFloat() * 128F, random.nextFloat() * 0.7F + 0.6F, false);

            for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                world.addParticle(ParticleTypes.BUBBLE_POP, (double)pos.getX() + 0.25F + random.nextFloat() * 0.5F, (double)pos.getY() + 0.9375, (double)pos.getZ() + 0.25F + random.nextFloat() * 0.5F, 0, 0.015625, 0);
            }
        }

        //TODO: mb make this method send a packet to check if the cauldron is cold or not
        /*else if (ThermalUtil.isCauldronCold(world, pos) && random.nextInt(5) == 0) {
            Direction direction = Direction.random(random);
            if (direction != Direction.UP) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos);
                if (!state.isOpaque() || !blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                double d = direction.getOffsetX() == 0 ? random.nextDouble() : (double)0.5F + (double)direction.getOffsetX() * 0.6;
                double e = direction.getOffsetY() == 0 ? random.nextDouble() : (double)0.5F + (double)direction.getOffsetY() * 0.6;
                double f = direction.getOffsetZ() == 0 ? random.nextDouble() : (double)0.5F + (double)direction.getOffsetZ() * 0.6;
                world.addParticle(ParticleTypes.DRIPPING_WATER, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + f, (double)0.0F, (double)0.0F, (double)0.0F);
                }
            }
        }*/
    }
}
