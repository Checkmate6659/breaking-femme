package com.breakingfemme.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MetalPipeItem extends Item {
    private final float attackDamage;
    public MetalPipeItem(Settings settings, float attackDamage) {
        super(settings);
        this.attackDamage = attackDamage;
    }

    @Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.breakingfemme.metal_pipe.tooltip"));
	}

	public float getAttackDamage() {
		return this.attackDamage;
	}

    @Override //NOTE: this method is on the SERVER! problem for playing sfx.
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        //anvil sound when hit
        //target.getWorld().playSound(target.getX(), target.getY(), target.getZ(), SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.MASTER, 4.0f, 1.0f, true);
        //target.playSound(SoundEvents.BLOCK_ANVIL_FALL, 4.0f, 1.0f);
        target.getWorld().playSound(null, new BlockPos((int)target.getX(), (int)target.getY(), (int)target.getZ()), SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.MASTER, 4.0f, 1.0f);
        //ServerPlayNetworkHandler.??.sendPacket(new PlaySoundS2CPacket(registryEntry, category, vec3d.getX(), vec3d.getY(), vec3d.getZ(), j, pitch, l));
        attacker.sendMessage(Text.literal("bonk tgt " + target.getWorld().isClient() + " atk " + attacker.getWorld().isClient()));
		return super.postHit(stack, target, attacker);
	}
}
