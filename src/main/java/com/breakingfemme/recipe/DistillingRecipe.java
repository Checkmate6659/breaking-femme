package com.breakingfemme.recipe;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.entity.FluidInventory;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

//recipe is for putting fluid in first slot into fluid in the last slot
//TODO: real distillations actually have more than one output, do this here too?
public class DistillingRecipe implements Recipe<FluidInventory> {
    private final Identifier id;
    private final FluidVariant input, output;
    private final int inputq, outputq;
    private final int gravel; //necessary gravel height in distillation column

    public DistillingRecipe(Identifier id, FluidVariant input, int input_quantity, FluidVariant output, int output_quantity, int gravel)
    {
        this.id = id;
        this.input = input;
        this.output = output;
        this.inputq = input_quantity;
        this.outputq = output_quantity;
        this.gravel = gravel;
    }

    public Pair<FluidVariant, Integer> getInput()
    {
        return new Pair<FluidVariant, Integer>(this.input, this.inputq);
    }

    public Pair<FluidVariant, Integer> getOutput()
    {
        return new Pair<FluidVariant, Integer>(this.output, this.outputq);
    }

    public int getMinimumGravelHeight()
    {
        return gravel;
    }

    @Override
    public boolean matches(FluidInventory inventory, World world) {
        if (inventory.size() < 2) return false;

        Pair<FluidVariant, Integer> fluid1 = inventory.getFluid(0);
        if(fluid1.getRight() < inputq || !fluid1.getLeft().equals(input)) return false;

        Pair<FluidVariant, Integer> fluid2 = inventory.getFluid(inventory.size() - 1);
        if(fluid2.getRight() + outputq > FluidConstants.BUCKET || (fluid2.getRight() > 0 && !fluid2.getLeft().equals(output))) return false; //TODO: less jank way, not to rely on fixed capacity!

        return true;
    }
    
    @Override
    public ItemStack craft(FluidInventory inventory, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
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
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<DistillingRecipe>
    {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "distilling"; //does this need to be unique between mods? i guess not, i got no warning
    }

    public static class Serializer implements RecipeSerializer<DistillingRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "distilling"; //name given in the json file

        @Override
        public DistillingRecipe read(Identifier id, JsonObject json)
        {
            NbtCompound nbt;
            try { //TODO: less janky way to do this shit!
                nbt = StringNbtReader.parse(json.toString());
            } catch (CommandSyntaxException e) {
                BreakingFemme.LOGGER.error("Invalid recipe encountered in " + id.toString());
                nbt = new NbtCompound(); //the reading will crash after this btw, just print an extra error message to help debug.
            }
            return new DistillingRecipe(id,
                BreakingFemme.fluidFromNbt(nbt.getCompound("input")),
                JsonHelper.getInt(JsonHelper.getObject(json, "input"), "quantity"),
                BreakingFemme.fluidFromNbt(nbt.getCompound("output")),
                JsonHelper.getInt(JsonHelper.getObject(json, "output"), "quantity"),
                JsonHelper.getInt(json, "gravel")
            );
        }

        @Override
        public DistillingRecipe read(Identifier id, PacketByteBuf buf)
        {
            FluidVariant input =  FluidVariant.fromPacket(buf);
            FluidVariant output = FluidVariant.fromPacket(buf);
            int inputq =  buf.readInt();
            int outputq = buf.readInt();
            int gravel = buf.readInt();

            return new DistillingRecipe(id, input, inputq, output, outputq, gravel);
        }

        @Override
        public void write(PacketByteBuf buf, DistillingRecipe recipe)
        {
            recipe.input.toPacket(buf);
            recipe.output.toPacket(buf);
            buf.writeInt(recipe.inputq);
            buf.writeInt(recipe.outputq);
            buf.writeInt(recipe.gravel);
        }
    }
}
