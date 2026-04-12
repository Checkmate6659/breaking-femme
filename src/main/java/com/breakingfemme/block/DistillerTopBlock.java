package com.breakingfemme.block;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.block.entity.DistillerTopBlockEntity;
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
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DistillerTopBlock extends BlockWithEntity {
    //TODO: make a property saying which fluid does it have inside it
    //TODO: make this into a thing compatible with the fabric fluid API
    private static final VoxelShape SHAPE;

    public DistillerTopBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new DistillerTopBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getStackInHand(hand);

        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.ofPlayerHand(player, hand));
        if(storage == null) return ActionResult.PASS; //idk if the != null is necessary

        BlockEntity be = world.getBlockEntity(pos);
        if(be == null) return ActionResult.FAIL; //the storage is valid, but for some reason the block entity isnt. so dont spill everywhere.
        if(!(be instanceof DistillerTopBlockEntity)) return ActionResult.FAIL; //same here
        DistillerTopBlockEntity distiller = (DistillerTopBlockEntity)be;

        if(storage.supportsInsertion())
        {
            try(Transaction transaction = Transaction.openOuter())
            {
                FluidVariant fluid = distiller.fluidStorage.variant;
                long transferred_amount = storage.insert(fluid, distiller.fluidStorage.amount, transaction);
                distiller.fluidStorage.extract(fluid, transferred_amount, transaction);
                transaction.commit();

                //play a sound effect! can/should it be item-dependent? like for bottles make it different
                world.playSoundFromEntity(null, player, FluidVariantAttributes.getFillSound(fluid), SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            catch (Exception e) {}

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    //TODO: add methods afterBreak, hasComparatorOutput and getComparatorOutput here

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.DISTILLER_TOP_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
    
    static {
        SHAPE = VoxelShapes.union(
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 11.0, 4.0),
            Block.createCuboidShape(2.0, 0.0, 12.0, 14.0, 11.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 4.0, 11.0, 14.0),
            Block.createCuboidShape(12.0, 0.0, 2.0, 14.0, 11.0, 14.0),
            Block.createCuboidShape(2.0, 1.0, 2.0, 14.0, 16.0, 14.0),
            Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 16.0, 16.0)
        );
    }
}
