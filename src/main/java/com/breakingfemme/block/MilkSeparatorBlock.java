package com.breakingfemme.block;

import com.breakingfemme.ModSounds;
import com.breakingfemme.item.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MilkSeparatorBlock extends Block {
    public static final BooleanProperty HAS_MILK = BooleanProperty.of("has_milk");
    public static final BooleanProperty HAS_SKIMMED_MILK = BooleanProperty.of("has_skimmed_milk");
    public static final BooleanProperty HAS_CREAM = BooleanProperty.of("has_cream");
    public static final IntProperty SPINS = IntProperty.of("spins", 0, 15); //need to click the thing 16 times to centrifuge it properly
    public static final BooleanProperty IS_SPINNING = BooleanProperty.of("spinning"); //and theres a bit of a cooldown
    protected static final int SPIN_TICKS = 20; //spins for 20 ticks (ie 1 second) on every click

    public MilkSeparatorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HAS_MILK, false).with(HAS_SKIMMED_MILK, false).with(HAS_CREAM, false).with(SPINS, 0).with(IS_SPINNING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(HAS_MILK);
        builder.add(HAS_SKIMMED_MILK);
        builder.add(HAS_CREAM);
        builder.add(SPINS);
        builder.add(IS_SPINNING);
    }

    //TODO: can we create an actual sloped shape? sth like this
    //https://github.com/copycats-plus/copycats/blob/multiloader/common/src/main/java/com/copycatsplus/copycats/content/copycat/slope/CopycatSlopeModelCore.java
    //NOTE: its net.minecraft.util.shape now, not net.minecraft.world.phys.shapes
    //next best thing would be approximation with cuboids
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(2.0, 6.0, 2.0, 14.0, 12.0, 14.0);
        //VoxelShapes.union can be used for more complex shapes btw, still everything is axis aligned
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if(world.isClient() || state.get(IS_SPINNING)) //don't do stuff on client side, or if already spinning
            return ActionResult.SUCCESS;

        Item in_hand = player.getStackInHand(hand).getItem();
        //filling machine with whole milk or collecting products (or collecting back accidentally inputted milk)
        if(in_hand == Items.MILK_BUCKET)
        {
            if(!state.get(HAS_SKIMMED_MILK))
            {
                world.setBlockState(pos, state.with(HAS_MILK, true).with(SPINS, 0)); //reset number of spins when adding the milk in
                player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS);
            }
        }
        else if(in_hand == Items.BUCKET)
        {
            if(state.get(HAS_SKIMMED_MILK))
            {
                world.setBlockState(pos, state.with(HAS_SKIMMED_MILK, false));
                player.setStackInHand(hand, new ItemStack(ModItems.SKIMMED_MILK_BUCKET));
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS);
            }
            else if(state.get(HAS_MILK))
            {
                world.setBlockState(pos, state.with(HAS_MILK, false));
                player.setStackInHand(hand, new ItemStack(Items.MILK_BUCKET));
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS);
            }
        }
        else if(in_hand == ModItems.INGOT_MOLD)
        {
            if(state.get(HAS_CREAM))
            {
                world.setBlockState(pos, state.with(HAS_CREAM, false));
                player.getStackInHand(hand).decrement(1);
                ItemStack itemStack = new ItemStack(ModItems.CREAMGOT_MOLD);
                if (!player.getInventory().insertStack(itemStack)) {
                    player.dropItem(itemStack, false);
                }
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS);
            }
        }
        //crank if not interacting with special items & there is milk in the machine
        else if(state.get(HAS_MILK))
        {
            world.playSound(null, pos, ModSounds.CRANK, SoundCategory.BLOCKS);

            player.addExhaustion(3.0f); //exhaust player A LOT (remove 6 hunger bars for 16 clicks: 4 exhaustion = 1/2 hunger bar)
            if(state.get(SPINS) == 15) //reached the end!
                world.setBlockState(pos, state.with(HAS_MILK, false).with(HAS_SKIMMED_MILK, true).with(HAS_CREAM, true).with(SPINS, 0).with(IS_SPINNING, true));
            else //add a spin, make it spinning for 16 ticks
                world.setBlockState(pos, state.cycle(SPINS).with(IS_SPINNING, true));
            world.scheduleBlockTick(pos, this, SPIN_TICKS);
        }

        return ActionResult.SUCCESS;
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(IS_SPINNING, false));
    }

    //random tick: undo a spin, the products recombine if you go take a break
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        int spins = state.get(SPINS);
        if(spins > 0 && !state.get(IS_SPINNING)) //don't undo a spin if the thing is literally in use
            world.setBlockState(pos, state.with(SPINS, spins - 1));
    }
}
