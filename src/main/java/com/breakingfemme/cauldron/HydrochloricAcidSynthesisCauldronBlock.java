package com.breakingfemme.cauldron;

import java.util.List;
import java.util.Map;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.ModFluids;
import com.breakingfemme.ModItems;
import com.breakingfemme.ThermalUtil;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;
import net.minecraft.world.event.GameEvent;

//https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
public class HydrochloricAcidSynthesisCauldronBlock extends AbstractCauldronBlock {
    //custom cauldron behavior
    public static Map<Item, CauldronBehavior> BEHAVIOR = CauldronBehavior.createMap();
    static {
        //vanilla fluids can replace this if needed
        CauldronBehavior.registerBucketBehavior(BEHAVIOR);
        
        //start cooking: sulfuric acid + salt -> hydrochloric acid (but inefficient). sodium bisulfate is just "lost" (voided) here. could add later.
        SulfuricAcidCauldronBlock.BEHAVIOR.put(ModItems.ROCK_SALT, (state, world, pos, player, hand, stack) -> {
            if(!world.isClient)
            {
                player.getStackInHand(hand).decrement(1);
                world.setBlockState(pos, ModFluids.HYDROCHLORIC_ACID_SYNTHESIS_CAULDRON.getDefaultState());
                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });
    }

    public HydrochloricAcidSynthesisCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, BEHAVIOR);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && this.isEntityTouchingFluid(state, pos, entity)) {
            //caustic fluid => damage entity
            entity.damage(new DamageSource(
                world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BreakingFemme.CORROSION)), 64.0f //don't mess with hot sulfuric acid
            );
        }
    }

    public boolean isFull(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }

    //needs to sit around for quite a while and be cooled.
    //if this block gets ticked and is not cold... it explodes. you have been warned.
    //suitably cooled water directly above the cauldron can then catch hydrochloric acid.
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //this reaction is highly exothermic! needs an ice bath
        if(!ThermalUtil.isCauldronCold(world, pos))
        {
            //boom.
            Vec3d center = pos.toCenterPos();
            world.createExplosion(null, center.getX(), center.getY(), center.getZ(), 4.2f, ExplosionSourceType.BLOCK);
            return;
        }

        //success condition
        if(random.nextInt(6) == 0)
        {
            world.setBlockState(pos, ModFluids.SLUDGE_CAULDRON.getDefaultState());
            BlockPos water_pos = pos.up();

            //catch HCl gas with water; TODO: have a dilute hydrochloric acid fluid that can be distilled, just like sulfuric acid
            boolean caught = false;
            if(world.getBlockState(water_pos).equals(Fluids.WATER.getStill(false).getBlockState()) && ThermalUtil.isCauldronCold(world, water_pos))
            {
                world.setBlockState(water_pos, ModFluids.STILL_HYDROCHLORIC_ACID.getDefaultState().getBlockState());
                caught = true;
            }

            //some HCl gas escapes! if caught is set to true then less damage will be dealt and smaller radius
            //inflict severe chemical burns to surrounding living entities, ie just damage them and set them on fire
            //closer entities will get more initial damage
            List<Entity> entities;
            if(caught)
                entities = world.getOtherEntities(null,
                    new Box(water_pos.add(-3, -3, -3), water_pos.add(4, 4, 4))); //max fitting sphere radius is 3.5; 2nd argument is excluded from the box!!
            else
                entities = world.getOtherEntities(null,
                    new Box(water_pos.add(-8, -8, -8), water_pos.add(9, 9, 9))); //max fitting sphere radius is 8.5

            final double sub = caught ? 10.448979591836734 : 1.7716262975778547; //check a few lines down the line
            entities.iterator().forEachRemaining(entity -> {
                if(!entity.isLiving()) return; //not living, not affected

                double dist2 = entity.getPos().squaredDistanceTo(water_pos.toCenterPos()); //squared distance
                if(dist2 < 1) dist2 = 1; //don't divide by zero.
                double damage = 128 / dist2;
                damage -= sub; //damage will be equal to 0 at distance (3.5 if caught and 9.5 if not caught)
                if(damage < 0) return; //don't do anything if far enough away

                damage *= 4; //TODO: factor out into previous shit.
                if(sub < 6.7) //same as if uncaught actually. can't be fucked to make another final variable.
                    damage *= 4;

                //apply damage
                entity.damage(new DamageSource(
                    world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BreakingFemme.CORROSION)), (float)damage
                );

                //set entities on fire. its basically the same calculation as the damage, so we reuse the damage.
                int seconds = Math.round((float)damage + 1.5f);
                entity.setOnFireFor(seconds);
            });
        }
    }

    //doing boiling effect, and TODO: condensation (dripping) effect when its cold
    //its always boiling btw. thats normal.
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(random.nextInt(3) == 0)
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.75, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 192F + random.nextFloat() * 128F, random.nextFloat() * 0.7F + 0.6F, false);

        for(int i = 0; i < random.nextInt(1) + 1; ++i) {
            world.addParticle(ParticleTypes.BUBBLE_POP, (double)pos.getX() + 0.25F + random.nextFloat() * 0.5F, (double)pos.getY() + 0.9375, (double)pos.getZ() + 0.25F + random.nextFloat() * 0.5F, 0, 0.015625, 0);
        }

        //TODO: mb make this method send a packet to check if the cauldron is cold or not
        /*else if (ThermalUtil.isCauldronCold(world, pos) && random.nextInt(5) == 0) {
            Direction direction = Direction.random(random);
            if (direction != Direction.UP) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos);
                if (!state.isOpaque() || !blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                double d = direction.getOffsetX() == 0 ? random.nextDouble() : (double)0.5F + (double)direction.getOffsetX() * 0.6;
                double e = direction.getOffsetY() == 0 ? random.nextDouble() : (double)0.5F + (double)direction.getOffsetY() * 0.6;
                double f = direction.getOffsetZ() == 0 ? random.nextDouble() : (double)0.5F + (double)direction.getOffsetZ() * 0.6;
                world.addParticle(ParticleTypes.DRIPPING_WATER, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + f, (double)0.0F, (double)0.0F, (double)0.0F);
                }
            }
        }*/
    }
}
