package com.breakingfemme.mixin;

import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.breakingfemme.KineticsAttachments;

@Mixin(PlayerEntity.class)
public class StaggeringSneakMixin {
    //mixin PlayerEntity.clipAtLedge to make player fall off ledges when trying to sneak
    @Inject(at = @At("RETURN"), method = "clipAtLedge", cancellable = true)
	private void breakingfemme$fallOffLedge(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = ((PlayerEntity)(Object)this);

        if(!player.getWorld().isClient()) //don't execute on the server
            return;

        float etoh = KineticsAttachments.getLevel(player, KineticsAttachments.ETHANOL);
        float ach = KineticsAttachments.getLevel(player, KineticsAttachments.ACETALDEHYDE);

        if(etoh + ach > 1.5f) //threshold and ach coef pulled out of my ass as usual
            cir.setReturnValue(false);

        //TODO: achievement for falling off a ledge
        //to check that, we need to call PlayerEntity.method_30263 (private), or reimpl it
        //its code: this.isOnGround() || this.fallDistance < this.getStepHeight() && !this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(0.0, (double)(this.fallDistance - this.getStepHeight()), 0.0))
        //only stuff visible from here, perfect
	}
}
