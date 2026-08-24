package com.breakingfemme.client.mixin;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.EntityAttachments;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AllayEntityModel.class)
public class AllayFeaturesMixin {
	@Unique
   	public ModelPart breakingfemme$features; //can't be final cuz constructor injector is *not* the constructor

    @Inject(method = "<init>(Lnet/minecraft/client/model/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
    private void breakingfemme$constructor_features(ModelPart root, CallbackInfo ci) {
		//fucking 3 NESTED LAYERS!!! DAAAAAAAAAMN!!! well fuck.
		//at least this method shouldn't get called *too* many times...
		if(root.hasChild("root") && root.getChild("root").hasChild("body") && root.getChild("root").getChild("body").hasChild("breakingfemme_features"))
			breakingfemme$features = root.getChild("root").getChild("body").getChild("breakingfemme_features");
		else
			BreakingFemme.LOGGER.error("ERROR: Some Breaking Femme features failed to load! (AllayFeaturesMixin)");
	}

    @WrapOperation(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/TexturedModelData;of(Lnet/minecraft/client/model/ModelData;II)Lnet/minecraft/client/model/TexturedModelData;"))
    private static TexturedModelData breakingfemme$getTexturedModelData_features(ModelData data, int w, int h, Operation<TexturedModelData> original) {
		data.getRoot().getChild("root").getChild("body").addChild("breakingfemme_features", ModelPartBuilder.create().uv(1, 11).cuboid(-1.5F, -1.0F, -0.875F, 3.0F, 1.0F, 1.0F, new Dilation(-0.00390625F)), ModelTransform.of(0.0F, 0.625F, -0.25F, 1.0F, 0.0F, 0.0F));
		return original.call(data, w, h);
    }

	
	//feature positioning in setAngles function
	//cant use animateModel. because for some odd reason the allay's setAngles just resets all transforms at the beginning!
	@Inject(method = "setAngles(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At("TAIL"))
    protected void breakingfemme$position_features(Entity entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
		//normalized offset goes from 0 (no extra features, or not applicable) to 1 (fully developed features)
		float normalized_offset = EntityAttachments.getNormalizedFeatureOffset(entity);

		//return prematurely if nothing to show for
		breakingfemme$features.hidden = (normalized_offset == 0);
		if(normalized_offset == 0)
			return;

		breakingfemme$features.setPivot(0.0F, 0.625F, 0.375F - 0.625F * normalized_offset);
	}
}
