package com.breakingfemme.mixin;

import java.util.List;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.breakingfemme.BreakingFemme;
import com.breakingfemme.EntityAttachments;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.entity.Entity;

@Mixin(value = WardenEntityModel.class)
public class WardenFeaturesMixin extends DummyFeaturesMixin {
	@Unique
   	public ModelPart breakingfemme$features; //can't be final cuz constructor injector is *not* the constructor

    @Inject(method = "<init>(Lnet/minecraft/client/model/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
    private void breakingfemme$constructor_features(ModelPart root, CallbackInfo ci) {
		//fucking 3 NESTED LAYERS!!! DAAAAAAAAAMN!!! well fuck.
		//at least this method shouldn't get called *too* many times...
		if(root.hasChild("bone") && root.getChild("bone").hasChild("body") && root.getChild("bone").getChild("body").hasChild("breakingfemme_features"))
			breakingfemme$features = root.getChild("bone").getChild("body").getChild("breakingfemme_features");
		else
			BreakingFemme.LOGGER.error("ERROR: Some Breaking Femme features failed to load! (WardenFeaturesMixin)");

		//TODO: ribcage? i mean real features are in front of the ribcage but idk doesnt look *that* good
	}

    @WrapOperation(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/TexturedModelData;of(Lnet/minecraft/client/model/ModelData;II)Lnet/minecraft/client/model/TexturedModelData;"))
    private static TexturedModelData breakingfemme$getTexturedModelData_features(ModelData data, int w, int h, Operation<TexturedModelData> original) {
		data.getRoot().getChild("bone").getChild("body").addChild("breakingfemme_features", ModelPartBuilder.create().uv(7, 13).cuboid(-9.0F, -1.0F, -0.875F, 18.0F, 4.0F, 4.0F, new Dilation(-0.00390625F)), ModelTransform.of(0.0F, -4.0F, -4.75F, 1.0F, 0.0F, 0.0F));
		return original.call(data, w, h);
    }

	@WrapMethod(method = "getBody")
	private List<ModelPart> breakingfemme$body_features(Operation<List<ModelPart>> original)
	{
		return Stream.concat(Stream.of(breakingfemme$features), original.call().stream()).toList();
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

		//WHY DOES THIS DO NOTHING
		breakingfemme$features.setPivot(0.0F, -4.0F, -2.25F - 2.5F * normalized_offset);

		//do mod compat thing
        original.call(entity, limbAngle, limbDistance, tickDelta);
	}
}
