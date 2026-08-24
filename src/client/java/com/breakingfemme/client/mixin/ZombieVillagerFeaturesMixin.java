package com.breakingfemme.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ZombieVillagerEntityModel.class)
public class ZombieVillagerFeaturesMixin {
    @WrapOperation(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/TexturedModelData;of(Lnet/minecraft/client/model/ModelData;II)Lnet/minecraft/client/model/TexturedModelData;"))
    private static TexturedModelData breakingfemme$getTexturedModelData_features(ModelData data, int w, int h, Operation<TexturedModelData> original) {
		//calling addChild when a child with that name already exists replaces it. the old one will get garbage collected.
		//also, zombie villagers also have that integrated jacket thing like illagers do. so we need to account for that.
		ModelPartData mpd = data.getRoot();
		mpd.addChild("breakingfemme_features", ModelPartBuilder.create().uv(20, 26).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, new Dilation(-0.00390625F)), ModelTransform.of(0.0F, 2.0F, -2.75F, 1.0F, 0.0F, 0.0F));
		mpd.addChild("breakingfemme_features_jacket", ModelPartBuilder.create().uv(4, 42).cuboid(-4.0F, -1.0F, -0.875F, 8.0F, 2.0F, 2.0F, new Dilation(0.25F - 0.00390625F)), ModelTransform.of(0.0F, 2.0F, -2.75F, 1.0F, 0.0F, 0.0F));

		return original.call(data, w, h);
    }

	
	//TODO: feature growth (position and angle) animation shall be done under setAngles
}
