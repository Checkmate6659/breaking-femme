package com.breakingfemme.block.entity;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.fluid.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DistillerBlockEntity extends BlockEntity implements EmptyInventory { //this one doesn't have an inventory (just fluids), and doesn't have a screen
    public FlowableFluid fluid = Fluids.LAVA;
    public int level = 81000; //goes from 0 (empty) to 81000 (full, 1 bucket); its measured in droplets (TODO: implement disasters if necessary)
    public float temperature = 20.0f; //temperature in °C (TODO: move temperature function to dedicated thermal utils class)

    public DistillerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISTILLER_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) //saving data from ingame to save
    {
        super.writeNbt(nbt);
        nbt.putString("content", BreakingFemme.nameOfFluid(fluid));
        nbt.putInt("level", level);
        nbt.putFloat("temperature", temperature);
    }

    @Override
    public void readNbt(NbtCompound nbt) //loading data from save to ingame
    {
        super.readNbt(nbt);
        fluid = BreakingFemme.fluidFromName(nbt.getString("content"));
        level = nbt.getInt("level");
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

        if(world.getTime() % 20 == 0)
            BreakingFemme.LOGGER.info("distiller base ticks at " + pos + " in state " + state);

        long a = world.getTime() % 100;
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

        level = (int)a * 810;

        markDirty(world, pos, state);
        world.updateListeners(pos, state, state, 0);
    }
}
