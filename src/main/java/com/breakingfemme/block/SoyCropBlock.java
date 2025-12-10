package com.breakingfemme.block;

import com.breakingfemme.item.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class SoyCropBlock extends CropBlock {
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;

    public SoyCropBlock(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    @Override
    public IntProperty getAgeProperty()
    {
        return AGE;
    }

    @Override
    public ItemConvertible getSeedsItem()
    {
        return ModItems.SOYBEANS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(AGE);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if(random.nextInt(4) == 0) //soy grows 4 times slower: it will become mature after twice the time of wheat
            super.randomTick(state, world, pos, random);
        
        //TODO (maybe): sick/dead soy, disease spreading, preventing it with copper sulfate
    }
}
