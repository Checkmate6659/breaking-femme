package com.breakingfemme.recipe;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.entity.FunnelBlockEntity;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FilteringRecipe implements Recipe<FunnelBlockEntity> {
    //if we want to make grinding take variable time/hunger, need to do it here
    private final Identifier id;
    public final FluidVariant input, output;
    public final int inputq, outputq;
    public final Item item_output; //we can change the type to make loot tables; could look at Block#getDroppedStacks
    private final int droplets_per_filter, droplets_per_item; //TODO: turn those into longs

    public FilteringRecipe(Identifier id, FluidVariant input, int input_quantity, FluidVariant output, int output_quantity, Item item_output, int droplets_per_filter, int droplets_per_item)
    {
        this.id = id;
        this.input = input;
        this.output = output;
        this.inputq = input_quantity;
        this.outputq = output_quantity;
        this.item_output = item_output;
        this.droplets_per_filter = droplets_per_filter;
        this.droplets_per_item = droplets_per_item;
    }

    public int dropletsPerFilter()
    {
        return droplets_per_filter; //how many droplets of this recipe destroy/damage a filter (first droplet damages, then wait this many, of the same recipe)
    }

    public int dropletsPerItem()
    {
        return droplets_per_item; //how many droplets yield 1 item (need this many, from the same recipe, to add to output. no freebies!)
    }

    //TODO: cache the results of these functions, and reset it when the blocks get updated :3
    //or when they're part of a transaction (hmmm how are we gonna do that?)
    //if we have a funnel, a tank above, and the fluid get pumped out, we need that to wipe the cache

    //slight optimization
    public static boolean storagesMissing(BlockPos pos, World world)
    {
        if(FluidStorage.SIDED.find(world, pos.up(), Direction.DOWN) == null) return true; //top storage missing
        if(FluidStorage.SIDED.find(world, pos.down(), Direction.UP) == null) return true; //bottom storage missing

        return false;
    }

    //get max amount of fluid that can be extracted from the top; return smallest possible step that's above inputq; if no such step exists return 0
    public long extractibleFromTop(BlockPos top_pos, World world)
    {
        Storage<FluidVariant> top_storage = FluidStorage.SIDED.find(world, top_pos, Direction.DOWN);

        if(top_storage == null) return 0;

        long max_amount = StorageUtil.simulateExtract(top_storage, input, Long.MAX_VALUE / 256, null);
        if(max_amount <= inputq) //cannot extract more than inputq: either its identical to inputq or its 0; need to recheck to get max amount
            return StorageUtil.simulateExtract(top_storage, input, inputq, null);

        //TODO: custom check for CauldronStorage to bypass potentially quite expensive checks

        //find smallest amount that's ok to extract
        long simq = StorageUtil.simulateExtract(top_storage, input, inputq, null);
        if(simq > 0) //no annoying shit! YES!
            return simq;
        
        long curq = inputq;
        while(simq == 0 && curq > 0) //assuming the steps are all the same size, we can just double the amount until we get >0 and that's it. the other condition is anti-overflow.
        {
            simq = StorageUtil.simulateExtract(top_storage, input, curq, null);
            curq += curq;
        }

        return simq;
    }

    //actually (try to) extract a certain amount of fluid. return the amount of fluid actually extracted.
    public long extractFromTop(BlockPos top_pos, World world, long amount)
    {
        Storage<FluidVariant> top_storage = FluidStorage.SIDED.find(world, top_pos, Direction.DOWN);
        if(top_storage != null) //storage was not null
        {
            try(Transaction trans = Transaction.openOuter())
            {
                long actual = top_storage.extract(input, amount, trans);
                trans.commit();
                return actual;
            }
            catch(Exception e)
            {
                return 0;
            }
        }

        return 0;
    }

    //get max amount of fluid that can be inserted into the bottom; return smallest possible step that's above outputq; if no such step exists return 0
    public long insertibleIntoBottom(BlockPos bottom_pos, World world)
    {
        Storage<FluidVariant> bottom_storage = FluidStorage.SIDED.find(world, bottom_pos, Direction.UP);
        if(bottom_storage == null) return 0;

        long max_amount = StorageUtil.simulateInsert(bottom_storage, output, Long.MAX_VALUE / 256, null);
        if(max_amount <= outputq) //cannot insert more than outputq: either its identical to outputq or its 0; need to recheck to get max amount
            return StorageUtil.simulateInsert(bottom_storage, output, outputq, null);

        //TODO: custom check for CauldronStorage to bypass potentially quite expensive checks

        //find smallest amount that's ok to extract
        long simq = StorageUtil.simulateInsert(bottom_storage, output, outputq, null);
        if(simq > 0) //no annoying shit! YES!
            return simq;
        
        long curq = outputq;
        while(simq == 0 && curq > 0) //assuming the steps are all the same size, we can just double the amount until we get >0 and that's it. the other condition is anti-overflow.
        {
            simq = StorageUtil.simulateInsert(bottom_storage, output, curq, null);
            curq += curq;
        }

        return simq;
    }

    //insert at most a certain amount of fluid into the bottom. return the amount of fluid actually inserted.
    public long insertIntoBottom(BlockPos bottom_pos, World world, long amount)
    {
        Storage<FluidVariant> bottom_storage = FluidStorage.SIDED.find(world, bottom_pos, Direction.UP);
        if(bottom_storage != null) //storage was not null
        {
            try(Transaction trans = Transaction.openOuter())
            {
                long actual = bottom_storage.insert(output, amount, trans);
                trans.commit();
                return actual;
            }
            catch(Exception e)
            {
                return 0;
            }
        }
        
        return 0;
    }

    //the time (in ticks) the recipe should take (higher 32 bits)
    //and the value for initial_topq (lower 32 bits)
    //inputq and outputq are speed parameters. if they're both 1, a bucket is gonna be filtered in about 3 days and 9 hours (mc time).
    public long getTimeAndInitialTopq(FunnelBlockEntity funnel, World world)
    {
        BlockPos pos = funnel.getPos();
        int insertable = (int)insertibleIntoBottom(pos.down(), world);
        int extractible = (int)extractibleFromTop(pos.up(), world);
        return
            ((long)Math.max((insertable + outputq - 1) / outputq, (extractible + inputq - 1) / inputq) << 32) | //we can get ceil(division as rationals) this way
            (long)Math.max(insertable * outputq / inputq, extractible);
    }

    @Override
    public boolean matches(FunnelBlockEntity funnel, World world) {
        BlockPos pos = funnel.getPos();
        return extractibleFromTop(pos.up(), world) != 0 && insertibleIntoBottom(pos.down(), world) != 0;
    }
    
    @Override
    public ItemStack craft(FunnelBlockEntity inventory, DynamicRegistryManager registryManager) {
        return new ItemStack(item_output);
    }

    @Override
    public boolean fits(int width, int height) {
        return true; //what is this even? my guess is its used for crafting table size (2*2 or 3*3)
    }
    //for some reason BookCloningRecipe only returns true if width and height are >= 3
    //mb its to stop people from duping books in the survival crafting thing? no it does work... idk then.

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return new ItemStack(item_output);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<FilteringRecipe>
    {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "filtering"; //does this need to be unique between mods? i guess not, i got no warning
    }

    public static class Serializer implements RecipeSerializer<FilteringRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "filtering"; //name given in the json file

        @Override
        public FilteringRecipe read(Identifier id, JsonObject json)
        {
            NbtCompound nbt;
            try { //TODO: less janky way to do this shit!
                nbt = StringNbtReader.parse(json.toString());
            } catch (CommandSyntaxException e) {
                BreakingFemme.LOGGER.error("Invalid recipe encountered in " + id.toString());
                nbt = new NbtCompound(); //the reading will crash after this btw, just print an extra error message to help debug.
            }
            return new FilteringRecipe(id,
                BreakingFemme.fluidFromNbt(nbt.getCompound("input")),
                JsonHelper.getInt(JsonHelper.getObject(json, "input"), "quantity"),
                BreakingFemme.fluidFromNbt(nbt.getCompound("output")),
                JsonHelper.getInt(JsonHelper.getObject(json, "output"), "quantity"),
                ShapedRecipe.getItem(JsonHelper.getObject(json, "item_output")),
                JsonHelper.getInt(json, "droplets_per_filter"),
                JsonHelper.getInt(json, "droplets_per_item")
            );
        }

        @Override
        public FilteringRecipe read(Identifier id, PacketByteBuf buf)
        {
            FluidVariant input = FluidVariant.fromPacket(buf);
            FluidVariant output = FluidVariant.fromPacket(buf);
            int inputq =  buf.readInt();
            int outputq = buf.readInt();
            ItemStack item_output = buf.readItemStack();
            int droplets_per_filter = buf.readInt();
            int droplets_per_item   = buf.readInt();

            return new FilteringRecipe(id, input, inputq, output, outputq, item_output.getItem(), droplets_per_filter, droplets_per_item);
        }

        @Override
        public void write(PacketByteBuf buf, FilteringRecipe recipe)
        {
            recipe.input.toPacket(buf);
            recipe.output.toPacket(buf);
            buf.writeInt(recipe.inputq);
            buf.writeInt(recipe.outputq);
            buf.writeItemStack(new ItemStack(recipe.item_output)); //cant write an item directly to a buffer, kinda hacky approach
            buf.writeInt(recipe.droplets_per_filter);
            buf.writeInt(recipe.droplets_per_item);
        }
    }
}
