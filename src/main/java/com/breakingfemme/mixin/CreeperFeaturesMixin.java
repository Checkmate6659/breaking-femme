package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.breakingfemme.BreakingFemme;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.CreeperEntityModel;

@Mixin(value = CreeperEntityModel.class)
public class CreeperFeaturesMixin {
	@Unique
   	public ModelPart breakingfemme$features; //can't be final cuz constructor injector is *not* the constructor

    @Inject(method = "<init>(Lnet/minecraft/client/model/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
    private void breakingfemme$constructor_features(ModelPart root, CallbackInfo ci) {
		if(root.hasChild("breakingfemme_features"))
			breakingfemme$features = root.getChild("breakingfemme_features");
		else
			BreakingFemme.LOGGER.error("ERROR: Some Breaking Femme explosive features failed to load!");
	}

    @WrapOperation(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/TexturedModelData;of(Lnet/minecraft/client/model/ModelData;II)Lnet/minecraft/client/model/TexturedModelData;"))
    private static TexturedModelData breakingfemme$getTexturedModelData_features(ModelData data, int w, int h, Operation<TexturedModelData> original) {
		//somehow dilation for exploding already works!
		//TODO: fix uv potentially!
		data.getRoot().addChild("breakingfemme_features", ModelPartBuilder.create().uv(18, 22).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, new Dilation(-0.00390625F)), ModelTransform.of(0.0F, 10.0F, -1.75F, 1.0F, 0.0F, 0.0F));
		return original.call(data, w, h);
    }

	
	//TODO: feature growth (position and angle) animation shall be done under setAngles
}
