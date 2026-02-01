package com.breakingfemme.recipe;

import java.util.ArrayList;
import java.util.List;

import com.breakingfemme.block.entity.ImplementedInventory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

public class FermentingRecipe implements Recipe<ImplementedInventory> {
    private final Identifier id;
    private final Ingredient input1, input2, input3, input4;
    private final ItemStack output; //we can change the type to make loot tables; could look at Block#getDroppedStacks
    private final boolean is_none; //for special NONE recipe
 
    //times/temperatures/mixing requirement
    private final int min_progress[] = {0, 0, 0, 0, 0}; //min progress before stage complete
    private final int max_progress[] = {0, 0, 0, 0, 0}; //max progress before risking failure (increasing grace timer)
    private final float min_temperature[] = {0, 0, 0, 0, 0};
    private final float max_temperature[] = {0, 0, 0, 0, 0};
    private final boolean should_mix[] = {false, false, false, false, false};

    //Take in 4 inputs, and lists of length 5 containing all stage information
    public FermentingRecipe(Identifier id, Ingredient input1, Ingredient input2, Ingredient input3, Ingredient input4, ItemStack output,
        List<Integer> min_progress, List<Integer> max_progress, List<Float> min_temperature, List<Float> max_temperature, List<Boolean> should_mix)
    {
        this.id = id;
        this.is_none = id.toString().equals("minecraft:air"); //if no recipe is running, we need a "no recipe" recipe. implemented kind of space-inefficiently.
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.input4 = input4;
        this.output = output;

        for(int i = 0; i < 5; i++)
        {
            this.min_progress[i] = min_progress.get(i);
            this.max_progress[i] = max_progress.get(i);
            this.min_temperature[i] = min_temperature.get(i);
            this.max_temperature[i] = max_temperature.get(i);
            this.should_mix[i] = should_mix.get(i);
        }
    }

    public static final FermentingRecipe NONE = new FermentingRecipe(Identifier.of("minecraft", "air"),
        Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY,
        List.of(0, 0, 0, 0, 0), List.of(0, 0, 0, 0, 0), List.of(0f, 0f, 0f, 0f, 0f), List.of(0f, 0f, 0f, 0f, 0f), List.of(false, false, false, false, false));

    public boolean isNone()
    {
        return is_none;
    }
    
    //can we assign each item to an ingredient?
    //we could formulate this as a graph matching problem and solve it with Hopcroft-Karp
    //but... this is n=4.
    private boolean assignable(ArrayList<Ingredient> ings, ArrayList<ItemStack> items)
    {
        int size = ings.size();
        if(size == 0)
            return true;

        ItemStack last_item = items.get(size - 1);
        items.remove(size - 1);
        for (int i = 0; i < size; i++)
        {
            Ingredient ing = ings.get(0);
            ings.remove(0); //remove from the beginning
            if(ing.test(last_item))
            {
                if(assignable(ings, items))
                    return true; //don't need to reset ings, as we're done anyway
            }
            ings.add(ing); //cycle ings array list to check all of the ingredients
        }
        //don't need to reset items, as we're done anyway

        return false;
    }

    @Override
    public boolean matches(ImplementedInventory inventory, World world) {
        //Fermenter input slots are 0, 1, 2 and 3
        //NOTE: this may change if we want to change shift-clicking behavior for water buckets in a lazy way
        //We do not want order to matter here, but if we allow extra items (not used in the recipe) then we would
        //have the possibility of multiple matches (ie sets of items to decrement) because of item tags.
        //By forbidding these cases, it is sufficient to decrement all non-empty slots in the FermenterBlockEntity class.
        //It is possible to do it better at scale, but since there are only 4 slots, it's probably gonna be permutation spam.

        if(is_none)
            return false;

        ArrayList<Ingredient> inputs = new ArrayList<Ingredient>(4);
        inputs.add(input1);
        inputs.add(input2);
        inputs.add(input3);
        inputs.add(input4);
    
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(4);
        stacks.add(inventory.getStack(0));
        stacks.add(inventory.getStack(1));
        stacks.add(inventory.getStack(2));
        stacks.add(inventory.getStack(3));

        return assignable(inputs, stacks);
    }
    
    //do given parameters meet conditions
    public boolean conditionsMet(int stage, int progress, float temperature, boolean is_mixing)
    {
        if(is_none)
            return false;

        return (is_mixing == this.should_mix[stage]) &&
            (progress <= this.max_progress[stage]) &&
            (temperature >= this.min_temperature[stage]) &&
            (temperature <= this.max_temperature[stage]);
    }

    //can we transition to the next stage (or finish if stage is 4)
    public boolean canTransition(int stage, int progress, float temperature)
    {
        if(is_none)
            return false;

        if(progress < this.min_progress[stage]) return false; //wait for longer!
        if(stage == 4) return true; //finish fermentation at any temperature
        return (temperature >= this.min_temperature[stage + 1]) &&
            (temperature <= this.max_temperature[stage + 1]); //only transition if temp correct for next stage
    }

    //which color should the bubble show up as in the interface when this stage is done
    //24 different colors, 0 white, 1 brown, 2 gray, 3 black etc. here 0 and 1 not used (for current stage)
    public int bubbleColor(int stage)
    {
        if(is_none)
            return 24; //do not show any bubbles when there is no recipe lol

        int middle = (int)(0.5f * (this.min_temperature[stage] + this.max_temperature[stage]));
        if(middle > 100) middle = 100; //max shown is 100°C
        else if(middle < 9) middle = 9; //min is 0-9°C interval, so we set it to 9°C
        //color logic: 0-9°C is highest (22), >=100°C is lowest (2), even numbers only; mixing adds 1
        return ((119 - middle) / 10) * 2 + (this.should_mix[stage] ? 1 : 0);
    }

    @Override
    public ItemStack craft(ImplementedInventory inventory, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

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

    public static class Type implements RecipeType<FermentingRecipe>
    {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "fermenting"; //does this need to be unique between mods? i guess not, i got no warning
    }

    public static class Serializer implements RecipeSerializer<FermentingRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "fermenting"; //name given in the json file

        @Override
        public FermentingRecipe read(Identifier id, JsonObject json)
        {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            List<JsonElement> input_raw = JsonHelper.getArray(json, "input").asList();
            List<Ingredient> input = new ArrayList<Ingredient>();
            for (JsonElement elt : input_raw)
                input.add(Ingredient.fromJson(elt));
            while(input.size() < 4)
                input.add(Ingredient.EMPTY);

            List<JsonElement> stages_raw = JsonHelper.getArray(json, "stages").asList();
            List<Integer> min_progress = new ArrayList<Integer>();
            List<Integer> max_progress = new ArrayList<Integer>();
            List<Float> min_temperature = new ArrayList<Float>();
            List<Float> max_temperature = new ArrayList<Float>();
            List<Boolean> should_mix = new ArrayList<Boolean>();
            for (JsonElement elt : stages_raw)
            {
                JsonObject eltobj = elt.getAsJsonObject();
                min_progress.add(JsonHelper.getInt(eltobj, "min_time"));
                max_progress.add(JsonHelper.getInt(eltobj, "max_time"));
                min_temperature.add(JsonHelper.getFloat(eltobj, "min_temperature"));
                max_temperature.add(JsonHelper.getFloat(eltobj, "max_temperature"));
                should_mix.add(JsonHelper.getBoolean(eltobj, "mix"));
            }
            while(min_progress.size() < 5) //finish "padding stages" instantly, last bubbles of the same color on the UI
            {
                min_progress.add(0);
                max_progress.add(42);
                min_temperature.add(min_temperature.get(min_temperature.size() - 1));
                max_temperature.add(max_temperature.get(max_temperature.size() - 1));
                should_mix.add(should_mix.get(should_mix.size() - 1));
            }

            return new FermentingRecipe(id, input.get(0), input.get(1), input.get(2), input.get(3), output,
                min_progress, max_progress, min_temperature, max_temperature, should_mix);
        }

        @Override
        public FermentingRecipe read(Identifier id, PacketByteBuf buf)
        {
            boolean is_none = buf.readBoolean();

            Ingredient input1 = Ingredient.fromPacket(buf);
            Ingredient input2 = Ingredient.fromPacket(buf);
            Ingredient input3 = Ingredient.fromPacket(buf);
            Ingredient input4 = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();

            int min_prog1 = buf.readInt();
            int min_prog2 = buf.readInt();
            int min_prog3 = buf.readInt();
            int min_prog4 = buf.readInt();
            int min_prog5 = buf.readInt();
            int max_prog1 = buf.readInt();
            int max_prog2 = buf.readInt();
            int max_prog3 = buf.readInt();
            int max_prog4 = buf.readInt();
            int max_prog5 = buf.readInt();
            float min_temp1 = buf.readFloat();
            float min_temp2 = buf.readFloat();
            float min_temp3 = buf.readFloat();
            float min_temp4 = buf.readFloat();
            float min_temp5 = buf.readFloat();
            float max_temp1 = buf.readFloat();
            float max_temp2 = buf.readFloat();
            float max_temp3 = buf.readFloat();
            float max_temp4 = buf.readFloat();
            float max_temp5 = buf.readFloat();
            boolean should_mix1 = buf.readBoolean();
            boolean should_mix2 = buf.readBoolean();
            boolean should_mix3 = buf.readBoolean();
            boolean should_mix4 = buf.readBoolean();
            boolean should_mix5 = buf.readBoolean();

            if(is_none)
                return FermentingRecipe.NONE;
            else
                return new FermentingRecipe(id, input1, input2, input3, input4, output,
                    List.of(min_prog1, min_prog2, min_prog3, min_prog4, min_prog5),
                    List.of(max_prog1, max_prog2, max_prog3, max_prog4, max_prog5),
                    List.of(min_temp1, min_temp2, min_temp3, min_temp4, min_temp5),
                    List.of(max_temp1, max_temp2, max_temp3, max_temp4, max_temp5),
                    List.of(should_mix1, should_mix2, should_mix3, should_mix4, should_mix5)
                );
        }

        @Override
        public void write(PacketByteBuf buf, FermentingRecipe recipe)
        {
            buf.writeBoolean(recipe.is_none);

            recipe.input1.write(buf);
            recipe.input2.write(buf);
            recipe.input3.write(buf);
            recipe.input4.write(buf);
            buf.writeItemStack(recipe.output);

            buf.writeInt(recipe.min_progress[0]);
            buf.writeInt(recipe.min_progress[1]);
            buf.writeInt(recipe.min_progress[2]);
            buf.writeInt(recipe.min_progress[3]);
            buf.writeInt(recipe.min_progress[4]);
            buf.writeInt(recipe.max_progress[0]);
            buf.writeInt(recipe.max_progress[1]);
            buf.writeInt(recipe.max_progress[2]);
            buf.writeInt(recipe.max_progress[3]);
            buf.writeInt(recipe.max_progress[4]);
            buf.writeFloat(recipe.min_temperature[0]);
            buf.writeFloat(recipe.min_temperature[1]);
            buf.writeFloat(recipe.min_temperature[2]);
            buf.writeFloat(recipe.min_temperature[3]);
            buf.writeFloat(recipe.min_temperature[4]);
            buf.writeFloat(recipe.max_temperature[0]);
            buf.writeFloat(recipe.max_temperature[1]);
            buf.writeFloat(recipe.max_temperature[2]);
            buf.writeFloat(recipe.max_temperature[3]);
            buf.writeFloat(recipe.max_temperature[4]);
            buf.writeBoolean(recipe.should_mix[0]);
            buf.writeBoolean(recipe.should_mix[1]);
            buf.writeBoolean(recipe.should_mix[2]);
            buf.writeBoolean(recipe.should_mix[3]);
            buf.writeBoolean(recipe.should_mix[4]);
        }
    }
}
