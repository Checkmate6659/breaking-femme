package com.breakingfemme.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.VillagerAttachments;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(VillagerEntity.class)
public class VillagerPickupMixin {
    @WrapOperation(at = @At(value = "INVOKE:FIRST", target = "contains"), method = "canGather")
	private boolean breakingfemme$canGrabGirlPotion(Set<Item> set, Object item, Operation<Boolean> operation) {
        VillagerEntity villager = (VillagerEntity)(Object)this;
        return (VillagerAttachments.isTransfem(villager) && VillagerAttachments.isEstrogen((Item)item))
            || operation.call(set, item);
    }

    //at what velocity should an item be dropped to land at the player's position? offset is player pos - villager pos
    //inspire from PlayerEntity.dropItem
    Vec3d breakingfemme$computeVelocity(Vec3d offset)
    {
        //translate the problem in 2 dimensions
        double vert = offset.getY();
        double horiz2 = offset.getX() * offset.getX() + offset.getZ() * offset.getZ();
        if(horiz2 < 1.52587890625e-05) //player above or below villager
            return new Vec3d(0, (vert > 0) ? 0.3 : -0.3, 0); //just throw above or below accordingly
        double horiz = Math.sqrt(horiz2); //will need both horiz and horiz2 later on

        //and now... we need to think.

        //item entities get 0.04 subtracted from y velocity every tick
        //if we denote C = cos theta and S = sin theta where theta is angle from horizontal, counted positively
        //we want to find a parabola going through (0, 0) and (horiz, vert) so basically
        //if it is the graph of Ax^x + Bx, then we have 2 equations:
        //horiz * (A * horiz + B) = vert (passing through villager position)
        //A = -0.444444... * (1 + B^2)

        //derivation of second condition:
        //x = v_0 * C * t
        //y = -g * t^2 + v_0 * S * t
        //and so t = x / (v_0 * C)
        //and so y = -g * x^2 / (v_0 C)^2 + S / C * x (btw S/C is tan theta)
        //so we need A = -0.4444... / C^2 (where 0.4444 = g/v_0^2)
        //and B = S / C. we can compute 1/C^2 from B like this: 1/C^2 = 1 + (S/C)^2
        //so the relationship is A = -0.44444... * (1 + B^2)

        //so really we need to find B such that horiz * (B - 0.4444... * (1 + B^2) * horiz) - vert = 0
        //this is a degree 2 equation in B, which can be expanded like this:
        //  -5/9 * horiz^2 * B^2  +  horiz * B  + (-5/9 * horiz^2 - vert)  = 0
        //this equation can be solved using the textbook quadratic formula

        double cB2 = -0.444444444444444444445 * horiz2; //precomputed 5/9; horiz2 != 0 guaranteed above, so this is not 0
        //cB1 is just horiz
        double cB0 = cB2 - vert; //theres a bit with the same expression as cB2

        double delta = horiz2 - 4 * cB2 * cB0;
        double b = 1; //if we cannot reach the player: throw at best launch angle (pi/4) in the direction of the player
        if(delta >= 0) //we can actually reach the player
            b = -0.5 * (horiz - Math.sqrt(delta)) / cB2; //prefer throwing items down => lower B

        //compute final item launching direction; NOTE: we can go through C and S and not need to compute theta
        //but i cant be fucked rn. mb im going to do that later.
        double theta = Math.atan(b); //b guaranteed not to be infinite because cB2 != 0 btw
        offset = offset.multiply(1, 0, 1).normalize().multiply(0.3 * Math.cos(theta)); //flatten and normalize the vector to 0.3 length, and start rotating
        return offset.add(0, 0.3 * Math.sin(theta), 0); //other part of the rotating
    }

    //in loot method, using ItemEntity.getOwner(), give some cash to the player who dropped it... if a player did, and is close enough
    //mb the closest player if theres a player close enough otherwise? idk.
    @Inject(at = @At("HEAD"), method = "loot")
    private void breakingfemme$payForGirlPotion(ItemEntity itemEntity, CallbackInfo ci)
    {
        Entity dealer = itemEntity.getOwner();
        if (dealer instanceof ServerPlayerEntity) //if item got thrown by a player who is online at the moment
        {
            //compute amount of emeralds to pay
            ItemStack dope = itemEntity.getStack();
            int value = dope.getCount() * VillagerAttachments.getValue(dope.getItem());

            //compute emerald velocity
            VillagerEntity villager = (VillagerEntity)(Object)this;
            Vec3d velocity = breakingfemme$computeVelocity(dealer.getPos().subtract(villager.getPos()));

            //actually pay
            World world = villager.getWorld();
            ItemStack cash = new ItemStack(Items.EMERALD, value);
            ItemEntity pile = new ItemEntity(world, villager.getX(), villager.getEyeY(), villager.getZ(),
                cash, velocity.getX(), velocity.getY(), velocity.getZ());
            world.spawnEntity(pile);
        }
    }

    @Inject(at = @At("HEAD"), method = "mobTick")
    private void breakingfemme$useGirlPotion(CallbackInfo ci)
    {
        //take time when using the estrogen
        VillagerEntity villager = (VillagerEntity)(Object)this;
        if(!VillagerAttachments.needsEstrogen(villager))
            return;

        SimpleInventory inventory = villager.getInventory();
        int size = inventory.size();

        int estro_slot = -1; //if this is equal to -1 at the end, we did not find any estrogen in the inventory
        int best_score = Integer.MAX_VALUE; //lowest score is better
        //TODO: smarter slot picking: consume from stack that is shortest to consume entirely => free up space asap
        for(int i = 0; i < size; i++)
        {
            ItemStack cur_stack = inventory.getStack(i);
            if(VillagerAttachments.isEstrogen(cur_stack.getItem())) //we found a slot with estrogen inside
            {
                int score = cur_stack.getCount() * VillagerAttachments.estrogenTime(cur_stack.getItem());
                if(score <= best_score)
                {
                    best_score = score;
                    estro_slot = i;
                }
            }
        }

        //consume an estrogen if possible, and increase the estrogen number
        if(estro_slot > -1)
        {
            ItemStack stack = inventory.getStack(estro_slot);
            int estro_time = VillagerAttachments.estrogenTime(stack.getItem());
            stack.decrement(1);
            inventory.setStack(estro_slot, stack);
            VillagerAttachments.giveEstrogenFor(villager, estro_time);

            //happy particles! would need that in loot actually, but this is like for debugging
            villager.getWorld().sendEntityStatus(villager, EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES);
        }
    }
}
