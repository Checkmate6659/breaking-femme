package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.breakingfemme.BreakingFemme;
import com.breakingfemme.EntityAttachments;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.Entity;

@Mixin(value = IllagerEntityModel.class)
public class IllagerFeaturesMixin extends DummyFeaturesMixin {
	@Unique
   	public ModelPart breakingfemme$features; //can't be final cuz constructor injector is *not* the constructor

	@Unique
	public ModelPart breakingfemme$features_jacket; //jacket layer, its kinda weird here but we need it.
   
    @Inject(method = "<init>(Lnet/minecraft/client/model/ModelPart;)V", at = @At("RETURN"))
    private void breakingfemme$constructor_features(ModelPart root, CallbackInfo ci) {
		if(root.hasChild("breakingfemme_features"))
			breakingfemme$features = root.getChild("breakingfemme_features");
		else
			BreakingFemme.LOGGER.error("ERROR: Some Breaking Femme features failed to load! (IllagerFeaturesMixin)");

		if(root.hasChild("breakingfemme_features_jacket"))
			breakingfemme$features_jacket = root.getChild("breakingfemme_features_jacket");
		else
			BreakingFemme.LOGGER.error("ERROR: Some Breaking Femme covered features failed to load! (IllagerFeaturesMixin)");
	}

    @WrapOperation(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/TexturedModelData;of(Lnet/minecraft/client/model/ModelData;II)Lnet/minecraft/client/model/TexturedModelData;"))
    private static TexturedModelData breakingfemme$getTexturedModelData_features(ModelData data, int w, int h, Operation<TexturedModelData> original) {
		//NOTE: with this setup we cant move features up/down depending on arm state (crossed or on the side)
		//we will need to do that in animation code
		//also the body of the illager has an integrated "jacket" for some reason. so we need to add the jacket in.
		//we could use that kinda stuff for our jacket actually, would simplify things potentially... maybe.
		data.getRoot().addChild("breakingfemme_features", ModelPartBuilder.create().uv(20, 26).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, new Dilation(-0.00390625F)), ModelTransform.of(0.0F, 2.0F, -2.75F, 1.0F, 0.0F, 0.0F));
		data.getRoot().addChild("breakingfemme_features_jacket", ModelPartBuilder.create().uv(4, 44).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, new Dilation(0.25F - 0.00390625F)), ModelTransform.of(0.0F, 2.0F, -2.75F, 1.0F, 0.0F, 0.0F));

		return original.call(data, w, h);
    }


	//feature positioning in animateModel function
	//literally the same as the villager code. but need to repeat code as we can't just reference that one
	//because of those all-important feature fields not being the same
	@Override
    protected void breakingfemme$animateModel(Entity entity, float limbAngle, float limbDistance, float tickDelta, Operation<Void> original) {
		//normalized offset goes from 0 (no extra features, or not applicable) to 1 (fully developed features)
		float normalized_offset = EntityAttachments.getNormalizedFeatureOffset(entity);

		//return prematurely if nothing to show for
		breakingfemme$features.hidden = (normalized_offset == 0);
		breakingfemme$features_jacket.hidden = breakingfemme$features.hidden;
		if(normalized_offset == 0)
			return;

		breakingfemme$features.setPivot(0.0F, 2.0F, -1.5F - 1.25F * normalized_offset);
		breakingfemme$features_jacket.setPivot(0.0F, 2.0F, -1.5F - 1.25F * normalized_offset);

		//do mod compat thing
        original.call(entity, limbAngle, limbDistance, tickDelta);
	}
}
