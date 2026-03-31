package com.breakingfemme.block.entity;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.recipe.DistillingRecipe;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DistillerBlockEntity extends BlockEntity implements FluidInventory { //this one doesn't have an inventory (just fluids), and doesn't have a screen
    private DefaultedList<Pair<FlowableFluid, Integer>> fluids = DefaultedList.ofSize(2, new Pair<FlowableFluid, Integer>(Fluids.WATER, Integer.valueOf(0)));
    public float temperature = -69420.0f; //temperature in °C (TODO: move temperature function to dedicated thermal utils class)

    public DistillerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISTILLER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<Pair<FlowableFluid, Integer>> getFluids() {
        return fluids;
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) //saving data from ingame to save
    {
        super.writeNbt(nbt);
        nbt.putString("base_content", BreakingFemme.nameOfFluid(fluids.get(0).getLeft()));
        nbt.putInt("base_level", fluids.get(0).getRight());
        nbt.putString("top_content", BreakingFemme.nameOfFluid(fluids.get(1).getLeft()));
        nbt.putInt("top_level", fluids.get(1).getRight());
        nbt.putFloat("temperature", temperature);
    }

    @Override
    public void readNbt(NbtCompound nbt) //loading data from save to ingame
    {
        super.readNbt(nbt);
        fluids.get(0).setLeft(BreakingFemme.fluidFromName(nbt.getString("base_content")));
        fluids.get(0).setRight(nbt.getInt("base_level"));
        fluids.get(1).setLeft(BreakingFemme.fluidFromName(nbt.getString("top_content")));
        fluids.get(1).setRight(nbt.getInt("top_level"));
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

    public void tick(World world, BlockPos pos, BlockState state)
    {
        if(world.isClient()) return;

        if(temperature == -69420.0f) //temperature uninitialized
            temperature = FermenterBlockEntity.environment_temperature(world, pos); //initialize to base temperature

        //if(world.getTime() % 20 == 0) //TODO: heating etc
        //    getFluid(0).setRight(69420);

        //TODO: bucket interaction (in the actual block ig?)
        //TODO: detecting top, we can check every tick if its still there or not, thats fine
        //TODO: distilling
        //TODO: this https://wiki.fabricmc.net/tutorial:transfer-api

        //Optional<DistillingRecipe> match = world.getRecipeManager()
        //    .getFirstMatch(DistillingRecipe.Type.INSTANCE, this, world);

        /*long a = world.getTime() % 100;
        if(a < 20)
            fluid = Fluids.LAVA;
        else if(a < 40)
            fluid = Fluids.WATER;
        else if(a < 60)
            fluid = ModFluids.STILL_BEER;
        else if(a < 80)
            fluid = ModFluids.STILL_TAR;
        else
            fluid = ModFluids.STILL_ANDROSTADIENEDIONE_OIL_SOLUTION;

        level = (int)a * 810;*/

        //markDirty(world, pos, state);
        //world.updateListeners(pos, state, state, 0); //not calling that on every tick, since that makes running 32k distillers without a job in parallel unbearably laggy, while this much is fine
        //TODO: call it when necessary. just not always at the same time. cuz this is what sends clients the data actually.
    }
}
