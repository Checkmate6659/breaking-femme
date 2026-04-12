package com.breakingfemme.block;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.entity.DistillerBlockEntity;
import com.breakingfemme.block.entity.ModBlockEntities;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

//block entity tutorial
//https://www.youtube.com/watch?v=Y4dK9ETdZCQ
public class DistillerBaseBlock extends BlockWithEntity {
    static final VoxelShape SHAPE;

    public DistillerBaseBlock(Settings settings) {
        super(settings);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    //Block entity code
    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new DistillerBlockEntity(pos, state);
    }

    static boolean done = false; //not thread-safe! but it should be fine ig...
    //still why does a fucking loop breaking variable have to be static, fuck java
    //also for now this is done on client AND server. didnt find better way to do it...
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getStackInHand(hand);

        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.ofPlayerHand(player, hand));
        if(storage == null) return ActionResult.PASS; //idk if the != null is necessary

        BlockEntity be = world.getBlockEntity(pos);
        if(be == null) return ActionResult.FAIL; //the storage is valid, but for some reason the block entity isnt. so dont spill everywhere.
        if(!(be instanceof DistillerBlockEntity)) return ActionResult.FAIL; //same here
        DistillerBlockEntity distiller = (DistillerBlockEntity)be;

        if(storage.supportsExtraction())
        {
            //we could use an exception to get out of this, but idk
            done = false; //need to reset done here because it needs to be... static. fucking java. why.
            storage.nonEmptyIterator().forEachRemaining(part -> {
                if(done) return; //just skip over all the elements
                FluidVariant fluid = part.getResource();

                try(Transaction transaction = Transaction.openOuter()) //try to extract some fluid from the item
                {
                    long initial_amount = part.getAmount();
                    long transferred_amount = distiller.fluidStorage.insert(fluid, part.getAmount(), transaction);
                    transferred_amount = part.extract(fluid, transferred_amount, transaction);
                    if(transferred_amount != initial_amount) //we need to transfer all the fluid!
                        transaction.abort();
                    else
                    {
                        transaction.commit();
                        done = true;

                        //play a sound effect! can/should it be item-dependent? like for bottles make it different
                        world.playSoundAtBlockCenter(pos, FluidVariantAttributes.getEmptySound(fluid), SoundCategory.BLOCKS, 1.0f, 1.0f, true);
                    }
                }
            });

            return ActionResult.SUCCESS; //swing hand, even if cannot insert
        }
        else if(storage.supportsInsertion())
        {
            try(Transaction transaction = Transaction.openOuter())
            {
                FluidVariant fluid = distiller.fluidStorage.variant;
                long transferred_amount = storage.insert(fluid, distiller.fluidStorage.amount, transaction);
                distiller.fluidStorage.extract(fluid, transferred_amount, transaction);
                transaction.commit();

                //play a sound effect! can/should it be item-dependent? like for bottles make it different
                world.playSoundAtBlockCenter(pos, FluidVariantAttributes.getFillSound(fluid), SoundCategory.BLOCKS, 1.0f, 1.0f, true);
            }
            catch (Exception e) {}

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    //what to do when block broken: spill fluid inside; place down a full block if full
    @Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.afterBreak(world, player, pos, state, blockEntity, tool);

        if (blockEntity instanceof DistillerBlockEntity distiller) {
            //spill fluid if not empty
            Pair<FluidVariant, Integer> fluid_pair = distiller.getFluid(0);
            int level = fluid_pair.getRight();
            if(fluid_pair.getLeft().getObject() instanceof FlowableFluid ffluid && level > 0)
                BreakingFemme.spillFluid(world, pos, ffluid, 8 - (level * 8) / 81000);

            //reset comparator output
            world.updateComparators(pos,this);
        }
    }

    //comparator output (from furnace)
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof DistillerBlockEntity blockEntity)
        {
            int level = blockEntity.getFluid(0).getRight();
            if(level == 0) //completely empty => don't send out anything.
                return 0;
            return 1 + (level * 14) / 81000;
        }
        return 0; //fallback (should never be reached)
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.DISTILLER_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
    
    static {
        SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 0, 16, 16, 2),
            Block.createCuboidShape(0, 0, 0, 2, 16, 16),
            Block.createCuboidShape(14, 0, 0, 16, 16, 16),
            Block.createCuboidShape(0, 0, 14, 16, 16, 16),
            Block.createCuboidShape(0, 0, 0, 16, 15, 16)
        );
    }
}
