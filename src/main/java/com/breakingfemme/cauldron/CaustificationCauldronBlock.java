package com.breakingfemme.cauldron;

import java.util.Map;
import java.util.Optional;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.datagen.ModItemTagProvider;
import com.breakingfemme.fluid.ModFluids;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class CaustificationCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        //vanilla fluids can replace this if needed
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        LyeWaterCauldronBlock.BEHAVIOR.put(ModBlocks.SLAKED_LIME.asItem(), (state, world, pos, player, hand, stack) -> { //shove the slaked lime in the lye water cauldron
            player.getStackInHand(hand).decrement(1);
            world.setBlockState(pos, ModFluids.CAUSTIFICATION_CAULDRON.getDefaultState());
            world.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            return ActionResult.success(world.isClient);
        });
    }

    public CaustificationCauldronBlock(AbstractBlock.Settings settings) {
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

    //in the real world, crude estrone would be recovered by filtration
    //we're gonna be using a cauldron - hopper - cauldron setup
    //automating that in vanilla would be quite a mess i imagine, need like chest/hopper minecarts or sth, and a piston system
    //cuz yes, the hopper would have to point down.
    //also, no flimsy filter paper, the stuff we filter here would make quick work of that.
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

        int idx_filter = -1;
        for(int i = 0; i < 5; i++)
        {
            ItemStack stk = hopper.getStack(i);
            if(idx_filter == -1 && stk.isIn(ModItemTagProvider.RESISTANT_FILTER)) //only resistant filters ok.
                idx_filter = i;
        }
        if(idx_filter == -1) return; //no filter found :(

        //use up or damage filter
        ItemStack filter = hopper.getStack(idx_filter);
        if(filter.isDamageable()) //does filter have durability
            filter.damage(4, world.random, null); //harsher chemical => more durability
        else
            filter.decrement(1); //consume a piece of filter

        //replace block states
        world.setBlockState(low_cauldron_pos, ModFluids.CAUSTIC_SODA_SOLUTION_CAULDRON.getDefaultState());
        world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
    }
}
