package com.breakingfemme.recipe;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.entity.FluidInventory;
import com.google.gson.JsonObject;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
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
    private final FlowableFluid input, output;
    private final int inputq, outputq;

    public DistillingRecipe(Identifier id, FlowableFluid input, int input_quantity, FlowableFluid output, int output_quantity)
    {
        this.id = id;
        this.input = input;
        this.output = output;
        this.inputq = input_quantity;
        this.outputq = output_quantity;
    }

    public Pair<FlowableFluid, Integer> getInput()
    {
        return new Pair<FlowableFluid, Integer>(this.input, this.inputq);
    }

    public Pair<FlowableFluid, Integer> getOutput()
    {
        return new Pair<FlowableFluid, Integer>(this.output, this.outputq);
    }

    @Override
    public boolean matches(FluidInventory inventory, World world) {
        if (inventory.size() < 2) return false;

        Pair<FlowableFluid, Integer> fluid1 = inventory.getFluid(0);
        if(fluid1.getRight() < inputq || !fluid1.getLeft().equals(input)) return false;

        Pair<FlowableFluid, Integer> fluid2 = inventory.getFluid(inventory.size() - 1);
        if(fluid2.getRight() < outputq || !fluid2.getLeft().equals(output)) return false;

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
            return new DistillingRecipe(id,
                BreakingFemme.fluidFromName(JsonHelper.getString(json, "input")),
                JsonHelper.getInt(json, "input_quantity"),
                BreakingFemme.fluidFromName(JsonHelper.getString(json, "output")),
                JsonHelper.getInt(json, "output_quantity")
            );
        }

        @Override
        public DistillingRecipe read(Identifier id, PacketByteBuf buf)
        {
            FlowableFluid input =  BreakingFemme.fluidFromName(buf.readString());
            FlowableFluid output = BreakingFemme.fluidFromName(buf.readString());
            int inputq =  buf.readInt();
            int outputq = buf.readInt();

            return new DistillingRecipe(id, input, inputq, output, outputq);
        }

        @Override
        public void write(PacketByteBuf buf, DistillingRecipe recipe)
        {
            buf.writeString(BreakingFemme.nameOfFluid(recipe.input));
            buf.writeString(BreakingFemme.nameOfFluid(recipe.output));
            buf.writeInt(recipe.inputq);
            buf.writeInt(recipe.outputq);
        }
    }
}
