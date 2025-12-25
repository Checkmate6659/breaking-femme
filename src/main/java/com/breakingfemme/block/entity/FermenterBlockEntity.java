package com.breakingfemme.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;
import com.breakingfemme.screen.FermenterScreenHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FermenterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private static final int OUTPUT_SLOT = 4; //output slot must be right after all input slots

    protected final PropertyDelegate propertyDelegate; //the PropertyDelegate is used for client<->server syncing

    //TODO: do custom recipe format, variable progress etc
    private int progress = 0;
    private int maxProgress = 1200; //1 minute (5 mc days = 120k ticks btw)
    
    public FermenterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERMENTER_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index)
                {
                    case 0 -> FermenterBlockEntity.this.progress;
                    case 1 -> FermenterBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index)
                {
                    case 0 -> FermenterBlockEntity.this.progress = value;
                    case 1 -> FermenterBlockEntity.this.maxProgress = value;
                };
            }

            @Override
            public int size() {
                return 2; //number of values to synchronize
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.entity.breakingfemme.fermenter");
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) //saving data from ingame to save
    {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("fermenter.progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) //loading data from save to ingame
    {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("fermenter.progress");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory pinv, PlayerEntity player) {
        return new FermenterScreenHandler(syncId, pinv, this, this.propertyDelegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    
    public void tick(World world, BlockPos pos, BlockState state)
    {
        if(world.isClient()) return;
        //this is where the fermenter logic goes

        //and basically copied from the tutorial, simpler than what i will be doing
        ItemStack output_stack = this.getStack(OUTPUT_SLOT);
        if(output_stack.isEmpty() || output_stack.getCount() < output_stack.getMaxCount())
        {
            if(hasRecipeWithOutput(output_stack))
            {
                progress++;
                markDirty(world, pos, state); //what does this do

                if(progress >= maxProgress)
                {
                    this.craftItem();

                    this.progress = 0; //reset progress
                }
            }
            else
                this.progress = 0; //reset progress
        }
        else
        {
            this.progress = 0; //reset progress
            markDirty(world, pos, state);
        }
    }

    //actual BLOCK ENTITY LOGIC
    //should just output a list of which items are in the input slots (not ItemStacks, remove empty stacks)
    //is it faster with items or with their raw ids (Item.getRawId)???
    private List<Item> getInputItems()
    {
        List<Item> inputs = new ArrayList<>(); //if i use List.of() its not mutable for some reason???
        for(int i = 0; i < OUTPUT_SLOT; i++) //don't touch output slot
            if(!inventory.get(i).isEmpty())
                inputs.add(inventory.get(i).getItem());
        return inputs;
    }

    private void craftItem()
    {
        //do this BEFORE removing items from input slots. otherwise unwrap problems.
        this.setStack(OUTPUT_SLOT, new ItemStack(resultItem().get(), getStack(OUTPUT_SLOT).getCount() + 1));

        //remove 1 item from each input slot (buckets????)
        for(int i = 0; i < OUTPUT_SLOT; i++)
            this.removeStack(i, 1);
    }

    private Optional<Item> resultItem()
    {
        //hardcoded recipes for now

        //nether wart + wheat + water bucket -> beer bucket

        //water bucket + milk bucket + sterols + nickel sulfate -> androstadienedione bucket (consumed bucket? fix that later)
        //mb replace water bucket with sugar? no issue with buckets then too
        if(getInputItems().containsAll(List.of(
            Items.WATER_BUCKET,
            Items.MILK_BUCKET,
            ModItems.STEROLS,
            ModItems.NICKEL_SULFATE
        )))
            return Optional.of(ModFluids.ANDROSTADIENEDIONE_BUCKET);

        return Optional.empty();
    }

    private boolean hasRecipeWithOutput(ItemStack output_stack)
    {
        Optional<Item> result = resultItem();
        if(result.isPresent())
            //if output slot is clogged, return false; also here we don't do stacking so enough room <=> output not full
            return output_stack.isEmpty() || output_stack.getItem() == result.get();

        return false;
    }
}
