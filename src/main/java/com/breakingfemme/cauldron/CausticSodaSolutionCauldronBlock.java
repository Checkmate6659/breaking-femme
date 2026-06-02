package com.breakingfemme.cauldron;

import java.util.Map;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.ThermalUtil;
import com.breakingfemme.fluid.ModFluids;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CausticSodaSolutionCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        CauldronBehavior FILL = (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.fillCauldron(world, pos, player, hand, stack, ModFluids.CAUSTIC_SODA_SOLUTION_CAULDRON.getDefaultState(), SoundEvents.ITEM_BUCKET_EMPTY);
        };

        //vanilla fluids
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ModFluids.CAUSTIC_SODA_SOLUTION_BUCKET, FILL);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ModFluids.CAUSTIC_SODA_SOLUTION_BUCKET, FILL);
        CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ModFluids.CAUSTIC_SODA_SOLUTION_BUCKET, FILL);
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.CAUSTIC_SODA_SOLUTION_BUCKET), (statex) -> {
                return true;
            }, SoundEvents.ITEM_BUCKET_FILL);
        });
    }

    public CausticSodaSolutionCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && this.isEntityTouchingFluid(state, pos, entity)) {
            //caustic fluid => damage entity
            entity.damage(new DamageSource(
                world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BreakingFemme.SODIUM)), 4.0f
            );
            
            //its still water, so do fire stuff. but keep burning.
            //only if we do sodium hydroxide extraction tho.
            /*if(entity.isOnFire())
            {
                if (entity.canModifyAt(world, pos)) {
                    this.onFireCollision(state, world, pos);
                }
            }*/
        }
    }

    //boil off water to concentrate the solution :3
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //can only boil if the block is hot (or if its in the nether, free heating lol)
        if(world.getDimension().ultrawarm() || ThermalUtil.isBlockHot(world, pos.down()))
            world.setBlockState(pos, ModFluids.CONCENTRATED_CAUSTIC_SODA_CAULDRON.getDefaultState());
    }

    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }

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
