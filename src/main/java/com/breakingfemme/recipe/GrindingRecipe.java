package com.breakingfemme.recipe;

import com.google.gson.JsonObject;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

//https://www.youtube.com/watch?v=4N5BY2aHins
//recipe is done in player inv directly, thus we use PlayerInventory (we would be using SimpleInventory usually)
public class GrindingRecipe implements Recipe<PlayerInventory> {
    //if we want to make grinding take variable time/hunger, need to do it here
    private final Identifier id;
    private final Ingredient input;
    private final ItemStack output; //we can change the type to make loot tables; could look at Block#getDroppedStacks

    public GrindingRecipe(Identifier id, Ingredient input, ItemStack output)
    {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(PlayerInventory inventory, World world) {
        return input.test(inventory.getStack(PlayerInventory.OFF_HAND_SLOT));
    }
    
    @Override
    public ItemStack craft(PlayerInventory inventory, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true; //what is this even? my guess is its used for crafting table size (2*2 or 3*3)
    }
    //for some reason BookCloningRecipe only returns true if width and height are >= 3
    //mb its to stop people from duping books in the survival crafting thing?

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<GrindingRecipe>
    {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "grinding"; //does this need to be unique between mods? i guess not, i got no warning
    }

    public static class Serializer implements RecipeSerializer<GrindingRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "grinding"; //name given in the json file

        @Override
        public GrindingRecipe read(Identifier id, JsonObject json)
        {
            Ingredient input = Ingredient.fromJson(JsonHelper.getObject(json, "input"));
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            return new GrindingRecipe(id, input, output);
        }

        @Override
        public GrindingRecipe read(Identifier id, PacketByteBuf buf)
        {
            Ingredient input = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();

            return new GrindingRecipe(id, input, output);
        }

        @Override
        public void write(PacketByteBuf buf, GrindingRecipe recipe)
        {
            recipe.input.write(buf);
            buf.writeItemStack(recipe.output);
        }
    }
}
