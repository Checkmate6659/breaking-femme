package com.breakingfemme.client.mixin;

import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

//TODO: try look at ArmorFeatureRenderer instead!!!
//especially the renderArmorParts method
//the item could be checked against a tag & check if flexibility enchant to see if boobs should be rendered (and mb against some ingame methods too, check if its actually a chestplate lol)
//and we have the vertex consumer and texture on hand, for free!


//line 56 issues?? the model variable is overwritten!!
//may need to modify the return value of getContextModel instead? idk
//or the rendering directly
//it is possible that I should have added the features by messing with the BipedEntityModel render method instead of this mixin? idk

@Mixin(value = ArmorFeatureRenderer.class)
public interface ArmorFeatureRendererTextureAccessor {
	@Accessor("armorTrimsAtlas")
	SpriteAtlasTexture breakingfemme$getArmorTrimsAtlas();

	@Invoker("getArmorTexture")
	Identifier breakingfemme$getArmorTexture(ArmorItem item, boolean secondLayer, @Nullable String overlay);
}
