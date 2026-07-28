package com.breakingfemme.cauldron;

import java.util.Map;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.ModFluids;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class HydrogenGeneratorCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        //vanilla fluids can replace this if needed
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            //if bucketing out, recover impure dilute sulfuric acid
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.IMPURE_DILUTE_SULFURIC_ACID_BUCKET), (statex) -> {
                return true;
            }, SoundEvents.ITEM_BUCKET_FILL);
        });

        //tossing an iron ingot in sulfuric acid starts the reaction
        SulfuricAcidCauldronBlock.BEHAVIOR.put(Items.IRON_INGOT, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.getStackInHand(hand).decrement(1);
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, ModFluids.HYDROGEN_GENERATOR_CAULDRON.getDefaultState());
                world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });
    }

    public HydrogenGeneratorCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && this.isEntityTouchingFluid(state, pos, entity)) {
            entity.damage(new DamageSource(
                world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BreakingFemme.CORROSION)), 16.0f
            );
        }
    }

    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }

    //turn into sterol solution after a little while (1/4 prob to go through for 6 mc hours average time)
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(random.nextInt(4) == 0)
            world.setBlockState(pos, ModFluids.IMPURE_DILUTE_SULFURIC_ACID_CAULDRON.getDefaultState());
    }

    //doing bubbling effect for hydrogen coming out of the iron ingot
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(random.nextInt(2) == 0)
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.75, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 192F + random.nextFloat() * 128F, random.nextFloat() * 0.7F + 0.6F, false);

        for(int i = 0; i < random.nextInt(3) + 1; ++i) { //a lot of bubbles!!
            world.addParticle(ParticleTypes.BUBBLE_POP, (double)pos.getX() + 0.25F + random.nextFloat() * 0.5F, (double)pos.getY() + 0.9375, (double)pos.getZ() + 0.25F + random.nextFloat() * 0.5F, 0, 0.015625, 0);
        }
    }
}
