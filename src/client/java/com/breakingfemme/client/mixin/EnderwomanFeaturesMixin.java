package com.breakingfemme.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EndermanEntityModel.class)
public class EnderwomanFeaturesMixin {
    @WrapOperation(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/TexturedModelData;of(Lnet/minecraft/client/model/ModelData;II)Lnet/minecraft/client/model/TexturedModelData;"))
    private static TexturedModelData breakingfemme$getTexturedModelData_features(ModelData data, int w, int h, Operation<TexturedModelData> original) {
		//calling addChild when a child with that name already exists replaces it. the old one will get garbage collected.
		data.getRoot().addChild("breakingfemme_features", ModelPartBuilder.create().uv(50, 22).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, new Dilation(-0.00390625F)), ModelTransform.of(0.0F, -5.0F, -1.75F, 1.0F, 0.0F, 0.0F));
		return original.call(data, w, h);
    }

	
	//TODO: feature growth (position and angle) animation shall be done under setAngles
}
