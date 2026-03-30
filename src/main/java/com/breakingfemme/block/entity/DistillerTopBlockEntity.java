package com.breakingfemme.block.entity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//TODO: not use my FluidInventory as it's kind of a piece of shit thats not needed here
public class DistillerTopBlockEntity extends BlockEntity implements FluidInventory { //just allows the fluids to get extracted
    public DistillerTopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISTILLER_TOP_BLOCK_ENTITY, pos, state);
    }

    //and get rid of this method.
    @Override
    public DefaultedList<Pair<FlowableFluid, Integer>> getFluids() {
        return DefaultedList.of();
    }

    //https://wiki.fabricmc.net/tutorial:transfer-api
    public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<FluidVariant>() {
        @Override
		protected FluidVariant getBlankVariant() {
			return FluidVariant.blank();
		}
 
		@Override
		protected long getCapacity(FluidVariant variant) {
			return FluidConstants.BUCKET / 81; //this thing needs to be in mB, not droplets. what.
		}
 
		@Override
		protected void onFinalCommit() {
			//called after a successful insertion or extraction, need markDirty to save
			markDirty();
            //https://wiki.fabricmc.net/tutorial:transfer-api_simpletank: we could send data here from server to client
		}
    };

    @Override
    protected void writeNbt(NbtCompound nbt) //saving data from ingame to save
    {
        super.writeNbt(nbt);
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

        //does anything need to be done here??
    }
}
