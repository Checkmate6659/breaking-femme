package com.breakingfemme.block.entity;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.BreakingFemme;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DistillerTopBlockEntity extends BlockEntity implements SidedStorageBlockEntity { //just allows the fluids to get extracted
    public DistillerTopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISTILLER_TOP_BLOCK_ENTITY, pos, state);
    }

    //https://wiki.fabricmc.net/tutorial:transfer-api
    //TODO: figure out why this doesnt just work
    //TODO: check this out https://github.com/Fabricators-of-Create/Create/blob/mc1.20.1/fabric/dev/src/main/java/com/simibubi/create/content/fluids/tank/FluidTankBlockEntity.java
    //or this rather https://github.com/Fabricators-of-Create/Create/blob/mc1.20.1/fabric/dev/src/main/java/com/simibubi/create/content/fluids/drain/ItemDrainBlockEntity.java
    //1.21 yt tutorial: https://www.youtube.com/watch?v=RCMkl3mJ55w
    //TODO: we could use SingleVariantStorage, or just not use withFixedCapacity mb, to be unable to insert, only extract
    public final SingleFluidStorage fluidStorage = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET,
        () -> {
            markDirty();
            //can send C2S packets here, if we were to do rendering, with updateListeners
        }
    );

    public Storage<FluidVariant> getFluidStorage(@Nullable Direction face)
    {
        BreakingFemme.LOGGER.info("getting fluid storage! " + fluidStorage);
        return fluidStorage;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) //saving data from ingame to save
    {
        super.writeNbt(nbt);
        //fluidStorage.writeNbt(nbt); //there is no corresponding readNbt method...
        nbt.put("fluidVariant", fluidStorage.variant.toNbt());
        nbt.putLong("amount", fluidStorage.amount);
    }

    @Override
    public void readNbt(NbtCompound nbt) //loading data from save to ingame
    {
        super.readNbt(nbt);
        fluidStorage.variant = FluidVariant.fromNbt(nbt.getCompound("fluidVariant"));
		fluidStorage.amount = nbt.getLong("amount");
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        if(world.isClient()) return;

        BreakingFemme.LOGGER.info("storage find result " + FluidStorage.SIDED.find(world, pos, Direction.UP));

        //does anything need to be done here??

        //this is a TEST!!
        //markDirty();
        //world.updateListeners(pos, state, state, 0); //WARNING: LAGGY!!!!!!
    }
}
