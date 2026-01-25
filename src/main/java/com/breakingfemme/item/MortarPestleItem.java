package com.breakingfemme.item;

import java.util.Optional;

import com.breakingfemme.ModSounds;
import com.breakingfemme.recipe.GrindingRecipe;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

//mortar and pestle item, for grinding things down
//hold the mortar and pestle in main hand, and item to grind in off hand
//check out this file and its neighbors
//https://github.com/Creators-of-Create/Create/blob/mc1.21.1/dev/src/main/java/com/simibubi/create/content/equipment/sandPaper/SandPaperItem.java
//how is bow done? for grinding animation
public class MortarPestleItem extends Item {
    public MortarPestleItem(Settings settings) {
        super(settings);
    }

    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    public UseAction getUseAction(ItemStack stack) { //used animation
        return UseAction.DRINK; //may need a custom renderer and shit, like create's sandpaper
    }

    @Override
	public SoundEvent getDrinkSound() {
		return ModSounds.GRINDING; //custom sound effect
	}

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(hand != Hand.MAIN_HAND) //must use main hand
            return TypedActionResult.fail(user.getStackInHand(hand));

        Optional<GrindingRecipe> match = world.getRecipeManager()
            .getFirstMatch(GrindingRecipe.Type.INSTANCE, user.getInventory(), world);
        if(!match.isPresent())
            return TypedActionResult.fail(user.getStackInHand(hand));

        return ItemUsage.consumeHeldItem(world, user, hand);
    }

	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        //spawn a particle every 4 ticks
        //it should be the type of particle of the offhand (item getting ground)
		if (world.isClient() && remainingUseTicks % 4 == 0)
        {
            ItemStackParticleEffect effect = new ItemStackParticleEffect(ParticleTypes.ITEM, user.getStackInHand(Hand.OFF_HAND));
            Vec3d location = user.getEyePos().add(user.getRotationVector().multiply(.5f)); //TODO: adjust location/speed
            world.addParticle(effect, location.x, location.y, location.z,
                world.random.nextDouble() * 0.25 - 0.125,
                world.random.nextDouble() * 0.25 - 0.125,
                world.random.nextDouble() * 0.25 - 0.125);
            
            return;
        }

        //make grinding exhausting
        if(user.isPlayer()) //really a non-player shouldn't be able to grind items, buuuuut... just be safe
            ((PlayerEntity)user).addExhaustion(0.0625f);
	}

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.USING_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (user instanceof PlayerEntity) {
            //get correct recipe (again) and its output item
            PlayerEntity playerEntity = (PlayerEntity)user;
            Optional<GrindingRecipe> match = world.getRecipeManager()
                .getFirstMatch(GrindingRecipe.Type.INSTANCE, playerEntity.getInventory(), world);
            if(!match.isPresent()) //a modded item that changed/disappeared should have been ground => do nothing.
                return stack;
            GrindingRecipe recipe = match.get(); //we know match.isPresent here

            ItemStack itemStack = recipe.getOutput(world.getRegistryManager());
            if (!playerEntity.getInventory().insertStack(itemStack)) {
                playerEntity.dropItem(itemStack, false);
            }

            //decrement offhand stack: consume item that must be ground
            if(!((PlayerEntity)user).getAbilities().creativeMode)
                user.getStackInHand(Hand.OFF_HAND).decrement(1);
        }

        //damage the mortar and pestle (possible to make damage depend on recipe btw)
        //like grinding metal could do more damage than grinding wheat
        //if done earlier, could depend on recipe (harder items damage the mortar and pestle more)
        stack.damage(1, user, (p) -> {
            p.sendToolBreakStatus(Hand.MAIN_HAND);
        });

        return stack;
    }
}
