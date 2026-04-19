package com.breakingfemme.cauldron;

import java.util.Map;

import com.breakingfemme.ThermalUtil;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class EstroneRecrystallizationCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        //vanilla fluids can replace this if needed
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            if(world.isClient) //if bucketing out, recover the 64% ethanol and the soybeans
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.125f, pos.getZ() + 0.5f, new ItemStack(ModItems.SOYBEANS)));
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.ET64_BUCKET), (statex) -> {
                return true;
            }, SoundEvents.ITEM_BUCKET_FILL);
        });
    }

    public EstroneRecrystallizationCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }

    //if cooled down, pop those estrone crystals off :3 (or rather the dust; TODO: add crystals that we then need to crush into powder)
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //this is supposed to take some time. but less if its COLD.
        if(ThermalUtil.isCauldronCold(world, pos) || (random.nextInt(3) == 0 && !(world.getDimension().ultrawarm() || ThermalUtil.isBlockHot(world, pos.down()))))
        {
            world.setBlockState(pos, ModFluids.SLUDGE_CAULDRON.getDefaultState()); //dirty ethanol -> sludge
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.125f, pos.getZ() + 0.5f, new ItemStack(ModItems.PURE_ESTRONE)));
        }
    }

    //doing boiling effect when its hot
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        //its capped off, so don't evaporate
        if(isFaceFullSquare(world.getBlockState(pos.up()).getCollisionShape(world, pos), Direction.DOWN))
            return;
        
        if ((world.getDimension().ultrawarm() || ThermalUtil.isBlockHot(world, pos.down())) && !ThermalUtil.isCauldronCold(world, pos)) {
            if(random.nextInt(3) == 0)
                world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.75, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 192F + random.nextFloat() * 128F, random.nextFloat() * 0.7F + 0.6F, false);

            for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                world.addParticle(ParticleTypes.BUBBLE_POP, (double)pos.getX() + 0.25F + random.nextFloat() * 0.5F, (double)pos.getY() + 0.9375, (double)pos.getZ() + 0.25F + random.nextFloat() * 0.5F, 0, 0.015625, 0);
            }
        }
    }
}
