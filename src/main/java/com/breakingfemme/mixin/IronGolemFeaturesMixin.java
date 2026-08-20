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
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.Entity;

@Mixin(value = IronGolemEntityModel.class)
public class IronGolemFeaturesMixin {
	@Unique
   	public ModelPart breakingfemme$features; //can't be final cuz constructor injector is *not* the constructor

    @Inject(method = "<init>(Lnet/minecraft/client/model/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
    private void breakingfemme$constructor_features(ModelPart root, CallbackInfo ci) {
		if(root.hasChild("breakingfemme_features"))
			breakingfemme$features = root.getChild("breakingfemme_features");
		else
			BreakingFemme.LOGGER.error("ERROR: Some Breaking Femme features failed to load! (IronGolemFeaturesMixin)");
	}

    @WrapOperation(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/TexturedModelData;of(Lnet/minecraft/client/model/ModelData;II)Lnet/minecraft/client/model/TexturedModelData;"))
    private static TexturedModelData breakingfemme$getTexturedModelData_features(ModelData data, int w, int h, Operation<TexturedModelData> original) {
		data.getRoot().addChild("breakingfemme_features", ModelPartBuilder.create().uv(8, 53).cuboid(-9.0F, -1.0F, -0.875F, 18.0F, 3.0F, 3.0F, new Dilation(-0.00390625F)), ModelTransform.of(0.0F, -4.0F, -6.375F, 1.0F, 0.0F, 0.0F));
		return original.call(data, w, h);
    }

	
	//feature positioning in animateModel function
	@Inject(method = "animateModel(Lnet/minecraft/entity/Entity;FFF)V", at = @At("HEAD"))
    protected void breakingfemme$animate_features(Entity entity, float limbAngle, float limbDistance, float tickDelta, CallbackInfo ci) {
		//normalized offset goes from 0 (no extra features, or not applicable) to 1 (fully developed features)
		float normalized_offset = EntityAttachments.getNormalizedFeatureOffset(entity);

		//return prematurely if nothing to show for
		breakingfemme$features.hidden = (normalized_offset == 0);
		if(normalized_offset == 0)
			return;

		breakingfemme$features.setPivot(0.0F, -4.0F, -4.5F - 1.875F * normalized_offset);
	}
}
