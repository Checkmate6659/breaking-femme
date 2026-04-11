package com.breakingfemme.block.entity;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.DistillerColumnBlock;
import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.recipe.DistillingRecipe;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DistillerBlockEntity extends BlockEntity implements FluidInventory { //this one doesn't have an inventory (just fluids), and doesn't have a screen
    private DefaultedList<Pair<FluidVariant, Integer>> fluids = DefaultedList.ofSize(2, new Pair<FluidVariant, Integer>(FluidVariant.blank(), Integer.valueOf(0)));
    public float temperature = -69420.0f; //temperature in °C (TODO: move temperature function to dedicated thermal utils class)
    //TODO: actually... you know... DO SOMETHING with the temperature. add multiple stages to the distillation?

    public DistillerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISTILLER_BLOCK_ENTITY, pos, state);
    }

    //the fluid inventory: slot 0 is base, slot 1 is top. only useful for recipes actually.
    @Override
    public DefaultedList<Pair<FluidVariant, Integer>> getFluids() {
        return fluids;
    }
    
    //Fluid variant storage
    //https://wiki.fabricmc.net/tutorial:transfer-api
    public final SingleFluidStorage fluidStorage = new SingleFluidStorage() {
        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidConstants.BUCKET;
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
            //can send C2S packets here
        }
    };

    public Storage<FluidVariant> getFluidStorage(@Nullable Direction face)
    {
        return fluidStorage;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) //saving data from ingame to save
    {
        super.writeNbt(nbt);
        nbt.put("fluidVariant", fluidStorage.variant.toNbt());
        nbt.putLong("amount", fluidStorage.amount);
        nbt.putFloat("temperature", temperature);
    }

    @Override
    public void readNbt(NbtCompound nbt) //loading data from save to ingame
    {
        super.readNbt(nbt);
        fluidStorage.variant = FluidVariant.fromNbt(nbt.getCompound("fluidVariant"));
		fluidStorage.amount = nbt.getLong("amount");
        temperature = nbt.getFloat("temperature");
    }

    //these next 2 methods sync the data between client and server
    //https://wiki.fabricmc.net/tutorial:blockentity_modify_data#sync_data_from_server_to_client
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    private void combine_fluids(int slot, Pair<FluidVariant, Integer> fluid, boolean remove)
    {
        Pair<FluidVariant, Integer> orig = fluids.get(slot);
        FluidVariant orig_fluid = orig.getLeft();
        int orig_amount = orig.getRight();
        FluidVariant added_fluid = fluid.getLeft();
        int added_amount = fluid.getRight();
        if(remove && orig_amount < added_amount)
        {
            BreakingFemme.LOGGER.error("Tried removing more fluid than what is possible, at DistillerBlockEntity at " + this.pos);
            added_amount = orig_amount; //try to stop having negative numbers as the quantity
        }
        if(!remove && orig_amount > 0 && !orig_fluid.equals(added_fluid)) //trying to combine different fluids: this is normal operation actually. it should just make sludge.
        {
            added_fluid = FluidVariant.of(ModFluids.STILL_SLUDGE);
        }

        int new_amount = orig_amount;
        if(remove) new_amount -= added_amount;
        else new_amount += added_amount;

        fluids.set(slot, new Pair<FluidVariant, Integer>(added_fluid, new_amount)); //added_fluid will always be the correct fluid, if there is no error
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        if(world.isClient()) return;

        if(world.getTime() % 4 != 0) return; //don't always check for stuff and do distilling (lag reduction mostly)

        if(temperature == -69420.0f) //temperature uninitialized
            temperature = FermenterBlockEntity.environment_temperature(world, pos); //initialize to base temperature

        //TODO: calculate heating, don't actually cook until hot enough!
        if(!BreakingFemme.isBlockHot(world, pos.down()))
            return;

        //TODO: bucket interaction (in the actual block ig?)
        //TODO: detecting top, we can check every tick if its still there or not, thats fine
        //TODO: distilling
        //TODO: this https://wiki.fabricmc.net/tutorial:transfer-api

        //copy internal fluid storage into inventory
        fluids.set(0, new Pair<FluidVariant, Integer>(this.fluidStorage.variant, (int)this.fluidStorage.amount));

        //find the distiller top block
        BlockPos top_pos = pos.up();
        boolean invalid = false;
        int gravel_height = 0;
        while(true) //TODO: decide and add a limit to how tall the distiller can be
        {
            BlockState column_state = world.getBlockState(top_pos);
            if(column_state.isOf(ModBlocks.DISTILLER_COLUMN))
            {
                //check if full of gravel for requirement
                if(column_state.get(DistillerColumnBlock.FULL))
                    gravel_height++;
            }
            else if(column_state.isOf(ModBlocks.DISTILLER_TOP))
            {
                //copy the fluid storage from the distiller top block
                if(world.getBlockEntity(top_pos) instanceof DistillerTopBlockEntity top_be)
                    fluids.set(1, new Pair<FluidVariant, Integer>(top_be.fluidStorage.variant, (int)top_be.fluidStorage.amount));
                else
                {
                    BreakingFemme.LOGGER.error("Distiller Top block entity not found at " + top_pos);
                    fluids.set(1, new Pair<FluidVariant, Integer>(FluidVariant.of(Fluids.WATER), 0));
                }

                //we found the top. that's great. now we keep going with the calculations.
                //also this is the only way to exit without setting invalid to true
                break;
            }
            else //invalid block state!! => invalid column
            {
                invalid = true;
                break;
            }

            top_pos = top_pos.up(); //next block!
        }

        //TODO: void slot 1 content before... any return. or just don't do it at all.

        if(invalid)
        {
            //TODO: mb do some stuff like fluid evaporation: the vapors just goes out in the air like that
            //and do a bunch of particles for *pollution*, mb bad potion effects (weakness, slowness, poison) when getting too close
            //but only if its out in the open
            return;
        }

        //get a matching recipe
        Optional<DistillingRecipe> match = world.getRecipeManager()
            .getFirstMatch(DistillingRecipe.Type.INSTANCE, this, world);

        if(!match.isPresent()) return; //we don't have a recipe
        DistillingRecipe recipe = match.get();

        //actually do a distilling step
        //TODO: *maybe* we could use fabric transactions to do this instead.
        //however if we want incompatible fluids in the distiller top to combine into making sludge, we need to do that by hand.
        Pair<FluidVariant, Integer> input = recipe.getInput();
        Pair<FluidVariant, Integer> output = recipe.getOutput();
        if(recipe.getMinimumGravelHeight() > gravel_height) //if not enough gravel in the column it should make sludge
            output.setLeft(FluidVariant.of(ModFluids.STILL_SLUDGE));

        combine_fluids(0, input, true);
        combine_fluids(1, output, false);

        //update the distiller base (inventory -> fluid storage)
        this.fluidStorage.variant = fluids.get(0).getLeft();
        this.fluidStorage.amount = fluids.get(0).getRight();

        //update the distiller top
        if(world.getBlockEntity(top_pos) instanceof DistillerTopBlockEntity top_be)
        {
            top_be.fluidStorage.variant = fluids.get(1).getLeft();
            top_be.fluidStorage.amount = fluids.get(1).getRight();
        }
        else
        {
            BreakingFemme.LOGGER.error("Distiller Top block entity not found at " + top_pos);
        }

        //this... might not be needed. but mb other mods would be like hey theres fluid here lets grab it. so just in case i void slot 1.
        //fluids.set(1, new Pair<FluidVariant, Integer>(FluidVariant.of(Fluids.WATER), 0));

        markDirty(); //still need to call this. we're not saving the inv, but we just changed the nbt.
        world.updateListeners(pos, state, state, 0); //not calling that on every tick, since that makes running 32k distillers without a job in parallel unbearably laggy, while this much is fine
        //TODO: call it when necessary. just not always at the same time. cuz this is what sends clients the data actually.
        //mb we *could* do something kinda dirty like only save once every 4 or 5 ticks, could do very slow and inefficient dupes ig... but meh.
    }
}
