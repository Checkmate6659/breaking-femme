package com.breakingfemme.client.mixin;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.EntityAttachments;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CreeperEntityModel.class)
public class CreeperFeaturesMixin extends DummyFeaturesMixin {
	@Unique
   	public ModelPart breakingfemme$features; //can't be final cuz constructor injector is *not* the constructor

    @Inject(method = "<init>(Lnet/minecraft/client/model/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
    private void breakingfemme$constructor_features(ModelPart root, CallbackInfo ci) {
		if(root.hasChild("breakingfemme_features"))
			breakingfemme$features = root.getChild("breakingfemme_features");
		else
			BreakingFemme.LOGGER.error("ERROR: Some Breaking Femme explosive features failed to load! (CreeperFeaturesMixin)");
	}

    @WrapOperation(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/TexturedModelData;of(Lnet/minecraft/client/model/ModelData;II)Lnet/minecraft/client/model/TexturedModelData;"))
    private static TexturedModelData breakingfemme$getTexturedModelData_features(ModelData data, int w, int h, Operation<TexturedModelData> original) {
		//somehow dilation for exploding already works!
		data.getRoot().addChild("breakingfemme_features", ModelPartBuilder.create().uv(18, 22).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, new Dilation(-0.00390625F)), ModelTransform.of(0.0F, 10.0F, -1.75F, 1.0F, 0.0F, 0.0F));
		return original.call(data, w, h);
    }

	
	//feature positioning in animateModel function
	@Override
    protected void breakingfemme$animateModel(Entity entity, float limbAngle, float limbDistance, float tickDelta, Operation<Void> original) {
		//normalized offset goes from 0 (no extra features, or not applicable) to 1 (fully developed features)
		float normalized_offset = EntityAttachments.getNormalizedFeatureOffset(entity);

		//return prematurely if nothing to show for
		breakingfemme$features.hidden = (normalized_offset == 0);
		if(normalized_offset == 0)
			return;

		breakingfemme$features.setPivot(0.0F, 10.0F, -0.5F - 1.25F * normalized_offset);

		//do mod compat thing
        original.call(entity, limbAngle, limbDistance, tickDelta);
	}
}
