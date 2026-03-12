package com.breakingfemme.block;

import com.breakingfemme.item.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class ChiliCropBlock extends CropBlock {
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;

    public ChiliCropBlock(Settings settings) {
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
        return ModItems.CHILI_PEPPER;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(AGE);
    }

    //chili only grows in warm climates
    //TODO: implement!! => move temperature check out of fermenter code, improve it to take caves into account etc
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return super.canPlaceAt(state, world, pos);
	}
}
