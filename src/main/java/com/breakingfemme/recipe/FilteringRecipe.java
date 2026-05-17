package com.breakingfemme.recipe;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.entity.FunnelBlockEntity;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
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
    private final FluidVariant input, output;
    private final int inputq, outputq;
    private final ItemStack item_output; //we can change the type to make loot tables; could look at Block#getDroppedStacks

    public FilteringRecipe(Identifier id, FluidVariant input, int input_quantity, FluidVariant output, int output_quantity, ItemStack item_output)
    {
        this.id = id;
        this.input = input;
        this.output = output;
        this.inputq = input_quantity;
        this.outputq = output_quantity;
        this.item_output = item_output;
    }

    //TODO: cache the results of these functions, and reset it when the blocks get updated :3
    //or when they're part of a transaction (hmmm how are we gonna do that?)
    //if we have a funnel, a tank above, and the fluid get pumped out, we need that to wipe the cache

    //get max amount of fluid that can be extracted from the top; at most inputq if storage, otherwise cauldron level amount.
    public long extractibleFromTop(BlockPos top_pos, World world)
    {
        Storage<FluidVariant> top_storage = FluidStorage.SIDED.find(world, top_pos, Direction.DOWN);
        if(top_storage != null) //storage was not null (so not a cauldron for instance)
            return StorageUtil.simulateExtract(top_storage, input, inputq, null);

        BlockState state = world.getBlockState(top_pos);
        CauldronFluidContent cfc = CauldronFluidContent.getForBlock(state.getBlock());
        if(cfc == null) //that is NOT a cauldron!
            return 0;
        if(!input.isOf(cfc.fluid)) //incorrect fluid (this is the top! we can't use an empty cauldron. empty cauldron is Fluids.EMPTY btw.)
            return 0;

        return cfc.amountPerLevel;
    }

    //actually (try to) extract a certain amount of fluid. return the amount of fluid actually extracted.
    public long extractFromTop(BlockPos top_pos, World world, long amount)
    {
        Storage<FluidVariant> top_storage = FluidStorage.SIDED.find(world, top_pos, Direction.DOWN);
        if(top_storage != null) //storage was not null (so not a cauldron for instance)
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
        else
        {
            BlockState state = world.getBlockState(top_pos);
            CauldronFluidContent cfc = CauldronFluidContent.getForBlock(state.getBlock());
            if(cfc == null) //that is NOT a cauldron!
                return 0;
            if(!input.isOf(cfc.fluid)) //incorrect fluid (this is the top! we can't use an empty cauldron. empty cauldron is Fluids.EMPTY btw.)
                return 0;

            //decrement level of fluid
            int level = cfc.currentLevel(state);
            if(level == 1) //we just sucked the cauldron dry => empty cauldron
                world.setBlockState(top_pos, Blocks.CAULDRON.getDefaultState());
            else //theres still fluid left: decrement level property but same block; in this case levelProperty cannot be null, otherwise we get level = 1.
                world.setBlockState(top_pos, state.with(cfc.levelProperty, level - 1));

            return cfc.amountPerLevel;
        }
    }

    //get max amount of fluid that can be inserted into the bottom; at most outputq if storage, otherwise cauldron level amount.
    public long insertibleIntoBottom(BlockPos bottom_pos, World world)
    {
        Storage<FluidVariant> bottom_storage = FluidStorage.SIDED.find(world, bottom_pos, Direction.UP);
        if(bottom_storage != null) //storage was not null (so not a cauldron for instance)
            return StorageUtil.simulateInsert(bottom_storage, output, outputq, null);

        BlockState state = world.getBlockState(bottom_pos);
        CauldronFluidContent cfc = CauldronFluidContent.getForBlock(state.getBlock());
        CauldronFluidContent cfc_f = CauldronFluidContent.getForFluid(output.getFluid());
        if(cfc == null) //that is NOT a cauldron!
            return 0;
        if(cfc_f == null)
            return 0; //an appropriate fluid containing cauldron does not exist => cant insert even into empty cauldron.
        if(cfc.fluid.equals(Fluids.EMPTY)) //empty cauldron => fits 1 bucket
            return cfc_f.amountPerLevel; //but really we're just adding 1 level. lol
        if(!input.isOf(cfc.fluid)) //incorrect fluid already inside, and its not empty.
            return 0;
        if(cfc.maxLevel == cfc.currentLevel(state)) //the bottom cauldron is full
            return 0;

        return cfc.amountPerLevel;
    }

    //insert at most a certain amount of fluid into the bottom. return the amount of fluid actually inserted.
    public long insertIntoBottom(BlockPos bottom_pos, World world, long amount)
    {
        Storage<FluidVariant> bottom_storage = FluidStorage.SIDED.find(world, bottom_pos, Direction.UP);
        if(bottom_storage != null) //storage was not null (so not a cauldron for instance)
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
        else
        {
            BlockState state = world.getBlockState(bottom_pos);
            CauldronFluidContent cfc = CauldronFluidContent.getForBlock(state.getBlock());
            CauldronFluidContent cfc_f = CauldronFluidContent.getForFluid(output.getFluid());
            if(cfc == null) //that is NOT a cauldron!
                return 0;
            if(cfc_f == null)
                return 0; //an appropriate fluid containing cauldron does not exist => cant insert even into empty cauldron.
            if(cfc.fluid.equals(Fluids.EMPTY)) //empty cauldron => fits 1 bucket
            {
                if(cfc.levelProperty == null) //we need a disjunction here, otherwise null won't get handled properly.
                    world.setBlockState(bottom_pos, cfc_f.block.getDefaultState());
                else
                    world.setBlockState(bottom_pos, cfc_f.block.getDefaultState().with(cfc.levelProperty, cfc.maxLevel));
                return cfc_f.amountPerLevel;
            }
            if(!input.isOf(cfc.fluid)) //incorrect fluid already inside, and its not empty.
                return 0;
            if(cfc.maxLevel == cfc.currentLevel(state)) //the bottom cauldron is full; this always happens if cfc.levelProperty is null.
                return 0;

            world.setBlockState(bottom_pos, state.cycle(cfc.levelProperty)); //cfc.levelProperty cannot be null here.
            return cfc.amountPerLevel;
        }
    }

    //the time (in ticks) the recipe should take
    //inputq and outputq are speed parameters. if they're both 1, a bucket is gonna be filtered in about 3 days and 9 hours (mc time).
    public int getTime(FunnelBlockEntity funnel, World world)
    {
        BlockPos pos = funnel.getPos();
        int insertable = (int)insertibleIntoBottom(pos.down(), world);
        int extractible = (int)extractibleFromTop(pos.up(), world);
        return Math.max((insertable + inputq - 1) / inputq, (extractible + outputq - 1) / outputq); //we can get ceil(division as rationals) this way
    }

    @Override
    public boolean matches(FunnelBlockEntity funnel, World world) {
        BlockPos pos = funnel.getPos();
        return extractibleFromTop(pos.up(), world) != 0 && insertibleIntoBottom(pos.down(), world) != 0;
    }
    
    @Override
    public ItemStack craft(FunnelBlockEntity inventory, DynamicRegistryManager registryManager) {
        return item_output;
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
        return item_output.copy();
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
                ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "item_output"))
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

            return new FilteringRecipe(id, input, inputq, output, outputq, item_output);
        }

        @Override
        public void write(PacketByteBuf buf, FilteringRecipe recipe)
        {
            recipe.input.toPacket(buf);
            recipe.output.toPacket(buf);
            buf.writeInt(recipe.inputq);
            buf.writeInt(recipe.outputq);
            buf.writeItemStack(recipe.item_output);
        }
    }
}
