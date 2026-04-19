package com.breakingfemme;

import com.breakingfemme.datagen.ModBlockTagProvider;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class ThermalUtil {
    //basically, is the block in the hot category or is it a lit furnace
	public static boolean isBlockHot(World world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		return state.isIn(ModBlockTagProvider.HOT) || state.isIn(ModBlockTagProvider.CREATE_HOT) ||
			(state.isIn(ModBlockTagProvider.FURNACE) && state.get(Properties.LIT));
	}

    //measure environment temperature
    //NOTE: doesn't take into account hot/cold blocks nearby, or if some panels of a fermenter are waterlogged
    //we should be able to waterlog panels to cool down the fermenter... but that should really be heat transfer idk...
    //but it should be in here too: air temp is usually higher than water temp
    //also, day temp higher than night temp... except if doing this in a cave/basement/cellar
    //that actually is a real thing, fermenting beer and wine in a cellar to stabilize the temperature
    //it turns out that the cool and stable temperatures from a cellar are perfect for making beer
    //TODO: come back
    public static float environment_temperature(World world, BlockPos pos)
    {
        DimensionType dimension = world.getDimension();
        Biome biome = world.getBiome(pos).value();

        float temperature = biome.getTemperature(); //TODO: this is not smooth by default. we should smooth it out a bit.
        temperature -= 0.00166667 * (pos.getY() - world.getSeaLevel()); //altitude decrease
        temperature = (temperature - 0.15f) * 20; //convert to celsius

        if(dimension.ultrawarm()) //Nether (and other modded "hot" dimensions)
        {
            temperature += 342; //would be quite a bit hot... probably not amazing to ferment stuff...
        }

        return temperature;
    }
}
