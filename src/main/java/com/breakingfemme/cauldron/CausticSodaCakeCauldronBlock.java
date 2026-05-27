package com.breakingfemme.cauldron;

import java.util.Map;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CausticSodaCakeCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        CauldronBehavior FILL = (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.fillCauldron(world, pos, player, hand, stack, ModFluids.CAUSTIC_SODA_CAKE_CAULDRON.getDefaultState(), SoundEvents.ITEM_BUCKET_EMPTY);
        };

        //vanilla fluids
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ModFluids.CAUSTIC_SODA_CAKE_BUCKET, FILL);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ModFluids.CAUSTIC_SODA_CAKE_BUCKET, FILL);
        CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ModFluids.CAUSTIC_SODA_CAKE_BUCKET, FILL);
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.CAUSTIC_SODA_CAKE_BUCKET), (statex) -> {
                return true;
            }, SoundEvents.ITEM_BUCKET_FILL);
        });
        LyeWaterCauldronBlock.BEHAVIOR.put(ModBlocks.SLAKED_LIME.asItem(), (state, world, pos, player, hand, stack) -> { //shove the slaked lime in the lye water cauldron
            player.getStackInHand(hand).decrement(1);
            world.setBlockState(pos, ModFluids.CAUSTIC_SODA_CAKE_CAULDRON.getDefaultState());
            world.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            return ActionResult.success(world.isClient);
        });

    }

    public CausticSodaCakeCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && this.isEntityTouchingFluid(state, pos, entity)) {
            entity.damage(new DamageSource(
                world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BreakingFemme.SODIUM)), 4.5f //intermediate damage + slaked lime damage
            );
        }
    }


    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }
}
