package com.breakingfemme.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.breakingfemme.BreakingFemme;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Identifier;

@Mixin(value = BipedEntityModel.class)
public class BipedFeaturesMixin {
	@Unique
   	public ModelPart breakingfemme$features; //can't be final cuz constructor injector is *not* the constructor
	//TODO: add features jacket layer... if it exists (PlayerEntityModel, VillagerResemblingModel...)
	//TODO: add features armor layer for vanilla-style armor, compat for vanilla-style armor, and compat mappings
	//TODO: add mob list (no skeletons)
	//for zombie villagers (mb other mobs?) the body's uv is different

    @Inject(method = "<init>(Lnet/minecraft/client/model/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
    private void breakingfemme$constructor_features(ModelPart root, Function<Identifier, RenderLayer> renderLayerFactory, CallbackInfo ci) {
		if(root.hasChild("breakingfemme_features"))
			breakingfemme$features = root.getChild("breakingfemme_features");
		else
			BreakingFemme.LOGGER.error("ERROR: Breaking Femme features failed to load!");
    }

    @ModifyReturnValue(method = "getModelData", at = @At("RETURN"))
    private static ModelData breakingfemme$getModelData_features(ModelData data, Dilation dilation, float pivotOffsetY) {
		//i think its radians!
		//TODO: zombie villager uv is fucked! add compat file and test out a buncha entities
		//also how do i make villager have them higher up and more forward or sth, to make them actually show
		//and not have boobs on skeletons
		//to adjust boob height, only change pivot y position! (for some odd reason that works)
		data.getRoot().addChild("breakingfemme_features", ModelPartBuilder.create().uv(18, 22).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, dilation.add(-0.00390625F)), ModelTransform.of(0.0F, 3.0F, -1.75F, 1.0F, pivotOffsetY, 0.0F));
		return data;
    }

	@SuppressWarnings("null")
	@WrapMethod(method = "getBodyParts")
	private Iterable<ModelPart> breakingfemme$getBodyParts_features(Operation<Iterable<ModelPart>> original) {
		return Iterables.concat(ImmutableList.of(breakingfemme$features), original.call());
	}

    @Inject(method = "setVisible", at = @At("HEAD"))
    private void breakingfemme$setVisible_features(boolean visible, CallbackInfo ci) {
		breakingfemme$features.visible = visible;
    }

	//in vanilla this method is only used to draw armor for some reason (need to look into armor rendering at some point)
    @Inject(method = "copyBipedStateTo", at = @At("RETURN"))
    private void breakingfemme$copy_features(BipedEntityModel<?> model, CallbackInfo ci) {
		((BipedFeaturesMixin)(Object)model).breakingfemme$features.copyTransform(breakingfemme$features);
    }

	
	//TODO: feature growth (position and angle) animation shall be done under setAngles
}
