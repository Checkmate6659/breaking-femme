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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
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

    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        //vanilla fluids can replace this if needed
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);

        //adding pure estrone (if the cauldron doesn't already have estrone)
        BEHAVIOR.put(ModItems.PURE_ESTRONE, (state, world, pos, player, hand, stack) -> {
            if(state.get(HAS_ESTRONE))
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
            if(state.get(HAS_RNICKEL))
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
        this.setDefaultState(this.stateManager.getDefaultState().with(HAS_ESTRONE, false).with(HAS_RNICKEL, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_ESTRONE, HAS_RNICKEL);
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

    //TODO: make it give crude estradiol and need cold (if too warm it might just give you low quality estrogen)

    /*//turn into sterol solution after a little while (random ticks, mb IntProperty for maceration start time)
    //optimal parameters for real sterol extraction from soybean: https://www.sciencedirect.com/science/article/pii/S2667010021002511
    //the real process takes about 2 hours, so here it should be 100 seconds, ie 2k ticks
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //theres a 2/3 chance of succeeding, which makes the expected value close to 100s at random tick speed 3
        //can only cook if the block is hot tho (or if its in the nether, free heating lol)
        if(random.nextInt(3) != 0 && (world.getDimension().ultrawarm() || ThermalUtil.isBlockHot(world, pos.down())))
            world.setBlockState(pos, ModFluids.STEROL_SOLUTION_CAULDRON.getDefaultState());
    }*/

    //doing boiling effect when its hot
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.getDimension().ultrawarm() || ThermalUtil.isBlockHot(world, pos.down())) {
            if(random.nextInt(3) == 0)
                world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.75, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 192F + random.nextFloat() * 128F, random.nextFloat() * 0.7F + 0.6F, false);

            for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                world.addParticle(ParticleTypes.BUBBLE_POP, (double)pos.getX() + 0.25F + random.nextFloat() * 0.5F, (double)pos.getY() + 0.9375, (double)pos.getZ() + 0.25F + random.nextFloat() * 0.5F, 0, 0.015625, 0);
            }
        }
    }
}
