package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.breakingfemme.VillagerAttachments;

import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(NameTagItem.class)
public class VillagerAssignMixin {
    @Inject(method = "useOnEntity", at = @At(value = "INVOKE", target = "setCustomName"))
    private void breakingfemme$getVillagerAngry(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir)
    {
        World world = entity.getWorld();
        if(!world.isClient() && entity instanceof VillagerEntity villager && VillagerAttachments.hasName(villager))
        {
            String name = villager.getAttached(VillagerAttachments.NAME);
            if(!name.equals(stack.getName().getString())) //trying to assign the villager a different name
            {
                //TODO: destroy street cred (when impld)
                ServerWorld sworld = (ServerWorld)world;
                sworld.handleInteraction(EntityInteraction.VILLAGER_KILLED, user, villager); //destroy rep
                sworld.handleInteraction(EntityInteraction.GOLEM_KILLED, user, villager); //anger nearby golems
                world.sendEntityStatus(villager, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES); //and angry particles
                world.sendEntityStatus(villager, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES); //twice. because very angry.
            }
        }
    }
}
