package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.breakingfemme.BreakingFemme;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;

@Mixin(value = VillagerResemblingModel.class)
public class VillagerFeaturesMixin {
	@Unique
   	public ModelPart breakingfemme$features; //can't be final cuz constructor injector is *not* the constructor

	@Unique
	public ModelPart breakingfemme$features_jacket; //jacket layer, always exists for villager-resembling models

    @Inject(method = "<init>(Lnet/minecraft/client/model/ModelPart;)V", at = @At("RETURN"))
    private void breakingfemme$constructor_features(ModelPart root, CallbackInfo ci) {
		if(root.hasChild("breakingfemme_features"))
			breakingfemme$features = root.getChild("breakingfemme_features");
		else
			BreakingFemme.LOGGER.error("ERROR: Some Breaking Femme features failed to load! (VillagerFeaturesMixin)");

		if(root.hasChild("breakingfemme_features_jacket"))
			breakingfemme$features_jacket = root.getChild("breakingfemme_features_jacket");
		else
			BreakingFemme.LOGGER.error("ERROR: Some Breaking Femme covered features failed to load! (VillagerFeaturesMixin)");
	}

    @ModifyReturnValue(method = "getModelData", at = @At("RETURN"))
    private static ModelData breakingfemme$getModelData_features(ModelData data) {
		ModelPartData mpd = data.getRoot();
		mpd.addChild("breakingfemme_features", ModelPartBuilder.create().uv(20, 26).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, new Dilation(-0.00390625F)), ModelTransform.of(0.0F, 2.0F, -2.75F, 1.0F, 0.0F, 0.0F));
		mpd.addChild("breakingfemme_features_jacket", ModelPartBuilder.create().uv(4, 42).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, new Dilation(0.25F - 0.00390625F)), ModelTransform.of(0.0F, 2.0F, -2.75F, 1.0F, 0.0F, 0.0F));

		return data;
    }

	
	//TODO: feature growth (position and angle) animation shall be done under setAngles
}
