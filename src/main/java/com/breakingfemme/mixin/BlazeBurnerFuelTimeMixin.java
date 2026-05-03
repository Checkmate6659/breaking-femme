package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.breakingfemme.datagen.ModItemTagProvider;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;

import net.minecraft.item.ItemStack;

//if create mod is not loaded, just fail harmlessly
//btw using IMixinConfigPlugin does NOT work. it will try to locate the class anyway and fail the same way.
//example for that btw:
//https://www.reddit.com/r/fabricmc/comments/tzz59c/conditionally_apply_mixin_if_another_mod_is/
//https://github.com/Juuxel/Adorn/blob/bd70a2955640897bc68ff1f4f201fe5e6c10bc32/fabric/src/main/java/juuxel/adorn/AdornMixinPlugin.java
@Mixin(BlazeBurnerBlockEntity.class)
public class BlazeBurnerFuelTimeMixin {
    @ModifyConstant(method = "tryUpdateFuel", constant = @Constant(intValue = 1600))
    private int breakingfemme$giveCustomFuelValues(int value, @Local ItemStack itemStack) {
        //if not in this tag, switch all these checks (to speed up stuff)
        if(itemStack.isIn(ModItemTagProvider.BLAZE_BURNER_FUEL_CUSTOM))
        {
            if(itemStack.isOf(ModItems.CRUDE_ESTRONE)) return 4800;
            if(itemStack.isOf(ModItems.PURE_ESTRONE)) return 6400;
            if(itemStack.isOf(ModFluids.ESTRONE_OIL_SOLUTION_BUCKET)) return 16000;
            if(itemStack.isOf(ModItems.PURE_ESTRADIOL_CRYSTALS)) return 32000;
            if(itemStack.isOf(ModItems.PURE_ESTRADIOL_POWDER)) return 32000;
            if(itemStack.isOf(ModItems.EGEL_BOTTLE)) return 12800;
        }

        return 1600;
    }
}
