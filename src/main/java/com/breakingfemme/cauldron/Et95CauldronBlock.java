package com.breakingfemme.cauldron;

import java.util.Map;

import com.breakingfemme.fluid.ModFluids;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class Et95CauldronBlock extends AbstractCauldronBlock {
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 3;
    public static final IntProperty LEVEL = Properties.LEVEL_3;

    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        CauldronBehavior FILL = (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.fillCauldron(world, pos, player, hand, stack, ModFluids.ET95_CAULDRON.getDefaultState().with(Et95CauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY);
        };

        //vanilla fluids
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ModFluids.ET95_BUCKET, FILL);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ModFluids.ET95_BUCKET, FILL);
        CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ModFluids.ET95_BUCKET, FILL);
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> {
            return CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModFluids.ET95_BUCKET), (statex) -> {
                return true;
            }, SoundEvents.ITEM_BUCKET_FILL);
        });

        //TODO: scoop up bottle

        //TODO: dissolve soy inside and leave it macerating (hey thats 70% ethanol! https://www.sciencedirect.com/science/article/pii/S2667010021002511)
    }

    public Et95CauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
        this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 3));
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
        return 2 * state.get(LEVEL);
    }
}
