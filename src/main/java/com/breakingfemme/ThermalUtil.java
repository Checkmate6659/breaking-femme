package com.breakingfemme;

import com.breakingfemme.datagen.ModBlockTagProvider;
import com.breakingfemme.mixin.BiomeAccessor;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;

public class ThermalUtil {
    //basically, is the block in the hot category or is it a lit furnace
	public static boolean isBlockHot(World world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		return state.isIn(ModBlockTagProvider.HOT) || state.isIn(ModBlockTagProvider.CREATE_HOT) ||
			(state.isIn(ModBlockTagProvider.FURNACE) && state.get(Properties.LIT));
	}

    public static boolean isCauldronCold(World world, BlockPos pos)
    {
        if(world.getDimension().ultrawarm() || ThermalUtil.isBlockHot(world, pos.down())) //its literally heated. cant be cold like that.
            return false;

        //count up cold blocks around/below the cauldron
        //TODO: count water/waterlogged blocks in cold biome, if cold biome then lower threshold
        //TODO: make any neighboring hot blocks make the cauldron not cold (mb corners ok tho idk)
        int cold_count = 0;
        for(int i = -1; i < 2; i++)
            for(int j = -1; j < 1; j++) //only count up to level of the cauldron, not blocks above
                for(int k = -1; k < 2; k++)
                    if(world.getBlockState(pos.add(i, j, k)).isIn(ModBlockTagProvider.COLD))
                        cold_count += 1 << (i*i + j*j + k*k - 1); //4 for neighbors, 2 for touching edge, 1 for touching corner

        return cold_count > 11; //TODO: change threshold based on biome coldness; mb even more or less effective cooling blocks
    }

    //Non-smooth function that calculates day and night temperature based on biome
    private static Pair<Float, Float> biomeTemperature(World world, BlockPos pos)
    {
        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);
        Biome biome = biomeEntry.value();
        float temperature = ((BiomeAccessor)biome).breakingfemme$getTemperature(pos);

        temperature -= 0.00166667 * (pos.getY() - world.getSeaLevel()); //altitude decrease
        temperature = (temperature - 0.15f) * 20; //convert to celsius

        //calculate surface altitude and dryness, from 0 to 1 both
        float surface_altitude = world.getTopY(Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()); //how high the biome is
        if(surface_altitude > pos.getY()) surface_altitude = pos.getY();
        surface_altitude -= world.getSeaLevel();
        surface_altitude *= 0.015625f; // *= 1/64
        if(surface_altitude < 0) surface_altitude = 0;
        if(surface_altitude > 1) surface_altitude = 1;

        float dryness = 0; //dryness is from 0 on 1
        if(!biomeEntry.isIn(BiomeTags.IS_OCEAN) && !biomeEntry.isIn(BiomeTags.IS_RIVER)) //water biomes!
            dryness = 1 - ((BiomeAccessor)biome).breakingfemme$getWeather().downfall();
        
        dryness = (float)Math.sqrt(dryness); //higher increase when dry, so for instance temp doesnt go <0 in plains
        surface_altitude *= 1 - ((1 - surface_altitude) * (1.25f - dryness) * 0.8f); //if very dry, "continental biome": as if altitude was higher
        surface_altitude = (float)Math.sqrt(surface_altitude); //higher increase at the bottom
        
        //diurnal temperature variation is greater at high altitude and in hotter and drier areas
        //high deserts have the most variation, while shores (low down, highest wetness) have the least
        //irl the stabilizing effects of an ocean on the weather can reach up to 100-200km... but we cant really do that here.
        float night_temperature = temperature - (surface_altitude + 0.5f) * (dryness + 0.125f) * 20;
        
        return new Pair<Float,Float>(temperature, night_temperature);
    }

    //measure environment temperature
    //NOTE: doesn't take into account hot/cold blocks nearby, or if some panels of a fermenter are waterlogged
    //we should be able to waterlog panels to cool down the fermenter... but that should really be heat transfer idk...
    //but it should be in here too: air temp is usually higher than water temp
    //also, day temp higher than night temp... except if doing this in a cave/basement/cellar
    //that actually is a real thing, fermenting beer and wine in a cellar to stabilize the temperature
    //it turns out that the cool and stable temperatures from a cellar are perfect for making beer
    public static float environment_temperature(World world, BlockPos pos)
    {
        Pair<Float, Float> day_night_temp = biomeTemperature(world, pos); //FIXME: not smooth!!

        //take depth into account
        int coverage = 20; //above this much coverage, kinda useless
        for(int i = -3; i <= 3; i++) //test in 7*7 area around target block
            for(int j = -3; j <= 3; j++)
            {
                BlockPos cpos = pos;

                int top_y = world.getTopY(Type.WORLD_SURFACE, cpos.getX(), cpos.getZ());
                int cur_coverage = 0;

                while(top_y > cpos.getY() && cur_coverage < coverage)
                {
                    cpos = cpos.up();
                    if(world.getBlockState(cpos).isIn(ModBlockTagProvider.STONES)) //count stone blocks in column
                        cur_coverage++;
                }

                if(cur_coverage < coverage) //take the worst one of the 49 columns
                    coverage = cur_coverage;
            }
        //temperature stabilization at higher depth
        //rule of thumb: temperature in cave close to surface = average air temperature during the whole year (or here during the whole day)
        float day_coef = 1.0f - coverage * 0.05f; //from 0 to 1 (for now this is just the instability)

        //calculate daytime, theres an extra 3 hour tick delay to mimic air thermal inertia
        day_coef *= Math.sin(((world.getTimeOfDay() + 3000) % 24000) * 0.0002617993877991494); //-1 to 1
        day_coef = (day_coef + 1) * 0.125f; //from 0 to 0.25
        day_coef *= 4 - world.getRainGradient(1); //effect of precipitation: slightly closer to night temp; now from 0 to 1
        float temperature = day_night_temp.getLeft() * day_coef + day_night_temp.getRight() * (1 - day_coef);

        //temperature increase at lower depth while in a cave; irl its 25-30°C/km, here we do 1°C every 8 blocks lol
        if(pos.getY() < world.getSeaLevel())
            temperature += (world.getSeaLevel() - pos.getY()) * coverage * 0.00625;

        if(world.getDimension().ultrawarm()) //Nether (and other modded "hot" dimensions)
            temperature += 342; //would be quite a bit hot... probably not amazing to ferment stuff...

        //TODO: hot/cold blocks

        //debug!!
        BreakingFemme.LOGGER.info("coverage " + coverage);
        BreakingFemme.LOGGER.info("day " + day_night_temp.getLeft() + " night " + day_night_temp.getRight());
        BreakingFemme.LOGGER.info("temperature " + temperature);

        return temperature;
    }
}
