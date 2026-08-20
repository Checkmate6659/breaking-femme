package com.breakingfemme.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.breakingfemme.BreakingFemme;
import com.breakingfemme.EntityAttachments;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

@Mixin(value = BipedEntityModel.class)
public class BipedFeaturesMixin {
	@Unique
   	public ModelPart breakingfemme$features; //can't be final cuz constructor injector is *not* the constructor

	@Unique
	public ModelPart breakingfemme$features_jacket; //jacket layer if it exists (PlayerEntityModel)
	//TODO: add features armor layer for vanilla-style armor, compat for vanilla-style armor, and compat mappings
	//TODO: add compat file and test out a buncha modded entities

    @Inject(method = "<init>(Lnet/minecraft/client/model/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
    private void breakingfemme$constructor_features(ModelPart root, Function<Identifier, RenderLayer> renderLayerFactory, CallbackInfo ci) {
		if(root.hasChild("breakingfemme_features"))
			breakingfemme$features = root.getChild("breakingfemme_features");
		else
			BreakingFemme.LOGGER.error("ERROR: Some Breaking Femme features failed to load! (BipedFeaturesMixin)");

		if(root.hasChild("breakingfemme_features_jacket")) //if not the case, it just doesn't exist and that's normal!
			breakingfemme$features_jacket = root.getChild("breakingfemme_features_jacket");
	}

    @ModifyReturnValue(method = "getModelData", at = @At("RETURN"))
    private static ModelData breakingfemme$getModelData_features(ModelData data, Dilation dilation, float pivotOffsetY) {
		//i think its radians! but still kinda weird idk.
		//to adjust boob height, only change pivot y position! (for some odd reason that works)

		ModelPartData mpd = data.getRoot();
		mpd.addChild("breakingfemme_features", ModelPartBuilder.create().uv(18, 22).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, dilation.add(-0.00390625F)), ModelTransform.of(0.0F, 3.0F, -1.75F, 1.0F, pivotOffsetY, 0.0F));
		if(mpd.getChild("jacket") != null)
			mpd.addChild("breakingfemme_features_jacket", ModelPartBuilder.create().uv(18, 38).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, dilation.add(0.24609375F)), ModelTransform.of(0.0F, 3.0F, -1.75F, 1.0F, pivotOffsetY, 0.0F));

		return data;
    }

	@SuppressWarnings("null")
	@WrapMethod(method = "getBodyParts")
	private Iterable<ModelPart> breakingfemme$getBodyParts_features(Operation<Iterable<ModelPart>> original) {
		if(breakingfemme$features_jacket == null)
			return Iterables.concat(ImmutableList.of(breakingfemme$features), original.call());
		return Iterables.concat(ImmutableList.of(breakingfemme$features, breakingfemme$features_jacket), original.call());
	}

    @Inject(method = "setVisible", at = @At("HEAD"))
    private void breakingfemme$setVisible_features(boolean visible, CallbackInfo ci) {
		breakingfemme$features.visible = visible;
		if(breakingfemme$features_jacket != null)
			breakingfemme$features_jacket.visible = visible;
    }

	//in vanilla this method is only used to draw armor for some reason (need to look into armor rendering at some point)
    @Inject(method = "copyBipedStateTo", at = @At("RETURN"))
    private void breakingfemme$copy_features(BipedEntityModel<?> model, CallbackInfo ci) {
		BipedFeaturesMixin model_mixin = (BipedFeaturesMixin)(Object)model;
		model_mixin.breakingfemme$features.copyTransform(breakingfemme$features);
		if(breakingfemme$features_jacket != null)
			model_mixin.breakingfemme$features_jacket.copyTransform(breakingfemme$features_jacket);
    }

	//feature positioning in animateModel function
	//public to allow other code to call this! and have less repeat code
	@Inject(method = "animateModel(Lnet/minecraft/entity/Entity;FFF)V", at = @At("HEAD"))
    public void breakingfemme$position_features(Entity entity, float limbAngle, float limbDistance, float tickDelta, CallbackInfo ci) {
		float advancement = 0; //goes from 0 (no extra features) to 1 (fully developed features)

		//if not estrogenable, don't show anything
		if(entity.getType().isIn(EntityAttachments.ESTROGENNABLE))
		{
			//5 second period. for testing purposes.
			//TODO: determine advancement based on entity's progress
			advancement = (entity.getWorld().getTime() % 100) * 0.01F;
		}

		//return prematurely if nothing to show for
		breakingfemme$features.hidden = (advancement == 0);
		if(breakingfemme$features_jacket != null)
			breakingfemme$features_jacket.hidden = breakingfemme$features.hidden;
		if(advancement == 0)
			return;

		//enderwoman and zombie villager fixes
		EntityType<?> type = entity.getType();
		float height = 3.0F;
		float jet = -0.5F;
		if(type.equals(EntityType.ENDERMAN))
			height = -11.0F;
		else if(type.equals(EntityType.ZOMBIE_VILLAGER))
		{
			height = 2.0F;
			jet = -1.5F;
		}

		breakingfemme$features.setPivot(0.0F, height, jet - 1.25F * advancement);
		if(breakingfemme$features_jacket != null)
			breakingfemme$features_jacket.setPivot(0.0F, height, jet - 1.25F * advancement);
	}
}
