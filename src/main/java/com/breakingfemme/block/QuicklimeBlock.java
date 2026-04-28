package com.breakingfemme.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.MapColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class QuicklimeBlock extends ConcretePowderBlock {
    public QuicklimeBlock(AbstractBlock.Settings settings) {
        super(ModBlocks.SLAKED_LIME, settings);
    }

    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return MapColor.PALE_YELLOW.color;
    }

    //otherwise placing directly in water would make it slake without exploding
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState();
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        onLanding(world, pos, state, state, null); //if in water, slake immediately; without this, can be placed in water and not slake
    }

    //slaking is highly exothermic!! => small explosion when it happens
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(!newState.isOf(ModBlocks.SLAKED_LIME)) return;
        Vec3d center = pos.toCenterPos();
        world.createExplosion(null, center.x, center.y, center.z, 1.0f, ExplosionSourceType.BLOCK);
    }

    public static boolean hardensIn(BlockState state) {
        return state.getFluidState().isOf(Fluids.WATER);
    }
}
