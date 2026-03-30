package com.breakingfemme.recipe;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.entity.EmptyInventory;
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
import net.minecraft.world.World;

//EmptyInventory because its only fluids!
public class DistillingRecipe implements Recipe<EmptyInventory> {
    private final Identifier id;
    private final FlowableFluid input, output;

    public DistillingRecipe(Identifier id, FlowableFluid input, FlowableFluid output)
    {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    public FlowableFluid getInput()
    {
        return this.input;
    }

    public FlowableFluid getOutput()
    {
        return this.output;
    }

    @Override
    public boolean matches(EmptyInventory inventory, World world) {
        return true; //TODO: real check??
    }
    
    @Override
    public ItemStack craft(EmptyInventory inventory, DynamicRegistryManager registryManager) {
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
                BreakingFemme.fluidFromName(JsonHelper.getString(json, "output"))
            );
        }

        @Override
        public DistillingRecipe read(Identifier id, PacketByteBuf buf)
        {
            FlowableFluid input =  BreakingFemme.fluidFromName(buf.readString());
            FlowableFluid output = BreakingFemme.fluidFromName(buf.readString());

            return new DistillingRecipe(id, input, output);
        }

        @Override
        public void write(PacketByteBuf buf, DistillingRecipe recipe)
        {
            buf.writeString(BreakingFemme.nameOfFluid(recipe.input));
            buf.writeString(BreakingFemme.nameOfFluid(recipe.output));
        }
    }
}
