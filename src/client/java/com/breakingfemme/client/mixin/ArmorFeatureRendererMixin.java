package com.breakingfemme.client.mixin;

import com.breakingfemme.EntityAttachments;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO: try look at ArmorFeatureRenderer instead!!!
//especially the renderArmorParts method
//the item could be checked against a tag & check if flexibility enchant to see if boobs should be rendered (and mb against some ingame methods too, check if its actually a chestplate lol)
//and we have the vertex consumer and texture on hand, for free!


//line 56 issues?? the model variable is overwritten!!
//may need to modify the return value of getContextModel instead? idk
//or the rendering directly
//it is possible that I should have added the features by messing with the BipedEntityModel render method instead of this mixin? idk

@Mixin(value = ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {
	@Unique
	private static LivingEntity target_entity;
	boolean should_render = false;

	@Inject(method = "renderArmor", at = @At("HEAD"))
	private void renderArmor_extractEntity(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot, int light, BipedEntityModel<LivingEntity> model, CallbackInfo info)
	{
		should_render = armorSlot == EquipmentSlot.CHEST; //only render on the chest

		target_entity = entity;
		if(!EntityAttachments.isEstrogenable(target_entity)) //not the right entity
			should_render = false;
	}

	@Unique
	private ModelPart getFeaturePart()
	{
		//create the extra part
		ModelPart part = new ModelPart(List.of(
			new ModelPart.Cuboid(18, 22,
				-4.0F, -1.0F, -2.875F, //position (relative to the model's transform)
				8.0F, 2.0F, 2.0F, //size of the cuboid
				1.0F, 1.0F, 1.0F, false, 64, 32, Set.of(Direction.values()))
		), Map.of());

		//TODO: do animation!!! (aka physics)
		//TODO: adjust growth parameters! it seems a bit off at the moment
		float normalized_offset = EntityAttachments.getNormalizedFeatureOffset(target_entity);
		float zero_offset = (target_entity.getType().equals(EntityType.ZOMBIE_VILLAGER)) ? -1.5F : -0.5F; //small zombie villager distinction

		//set the pivot and rotate it in place
		part.setPivot(0.0F, 1.0F, zero_offset - 1.25F * normalized_offset); //the real y position adjuster is this thing's y actually :3
		part.rotate(new Vector3f(1.0F, 0.0F, 0.0F));

		return part;
	}

	//3 injectors to inject the model into the renderer
	//literal estrogen injection :3

    @Inject(method = "renderArmorParts", at = @At("TAIL"))
	private void renderArmorParts_features(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, BipedEntityModel<LivingEntity> model, boolean secondTextureLayer, float red, float green, float blue, @Nullable String overlay, CallbackInfo info)
	{
		if(!should_render)
			return;

		Identifier texture = ((ArmorFeatureRendererTextureAccessor)(Object)this).breakingfemme$getArmorTexture(item, secondTextureLayer, overlay);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(texture));
		getFeaturePart().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
	}

    @Inject(method = "renderTrim", at = @At("TAIL"))
	private void renderTrim_features(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<LivingEntity> model, boolean leggings, CallbackInfo info)
	{
		if(!should_render)
			return;

		Sprite sprite = ((ArmorFeatureRendererTextureAccessor)(Object)this).breakingfemme$getArmorTrimsAtlas().getSprite(trim.getGenericModelId(material));
		VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims()));
		getFeaturePart().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
	}

    @Inject(method = "renderGlint", at = @At("TAIL"))
	private void renderGlint_features(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, BipedEntityModel<LivingEntity> model, CallbackInfo info)
	{
		if(!should_render)
			return;

		getFeaturePart().render(matrices, vertexConsumers.getBuffer(RenderLayer.getArmorEntityGlint()), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
