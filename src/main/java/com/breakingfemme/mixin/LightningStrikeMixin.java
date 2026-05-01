package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.ModBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LightningEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

//when lightning strikes somewhere, pick a gaussian random delta (but rounded to blocks), and if a ruby block:
//-is not touching water
//-is next to a granit block not touching water but touching a coal block, and such that all touching coal blocks do not touch water
//-is next to a coal block touching water
//then theres a probability (depends on distance of ruby block to lightning) that the ruby partially reduces

@Mixin(LightningEntity.class)
public class LightningStrikeMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "emitGameEvent"))
    private void breakingfemme$smeltRubies(CallbackInfo ci)
    {
        LightningEntity lightning = (LightningEntity)(Object)this;
        World world = lightning.getWorld();
        Random rng = world.random;
        BlockPos pos = lightning.getBlockPos();
        
        for(int i = 0; i < 256; i++)
        {
            Vec3d delta = new Vec3d(rng.nextGaussian() * 8, rng.nextGaussian() * 8, rng.nextGaussian() * 8);
            while(delta.lengthSquared() > 256) //cant go further than 16 blocks
                delta = new Vec3d(rng.nextGaussian() * 8, rng.nextGaussian() * 8, rng.nextGaussian() * 8);
            
            BlockPos tgt = BlockPos.ofFloored(delta.add(Vec3d.ofCenter(pos)));
            BlockState state = world.getBlockState(tgt);
            if(!state.isOf(ModBlocks.RUBY_BLOCK)) continue; //first check FAILED! its not ruby block

            boolean waterFailure = false; //ruby touches water?
            boolean coalFound = false; //found coal neighbor (and it touches water)?
            boolean graniteFound = false; //found granite neighbor (and it passes its checks)?
            for(BlockPos nei : BreakingFemme.getNeighbors(tgt))
            {
                state = world.getBlockState(nei);
                if(state.getFluidState().isOf(Fluids.WATER))
                {
                    waterFailure = true;
                    break;
                }
                else if(state.isOf(Blocks.COAL_BLOCK) && !coalFound)
                {
                    for(BlockPos nei2 : BreakingFemme.getNeighbors(nei)) //does it have water next to it
                    {
                        if(world.getFluidState(nei2).isOf(Fluids.WATER)) //yes: we got it!!
                        {
                            coalFound = true;
                            break;
                        }
                    }
                }
                else if(state.isOf(Blocks.GRANITE) && !graniteFound)
                {
                    boolean otherCoalFound = false;
                    boolean canStillBeFine = true;
                    for(BlockPos nei2 : BreakingFemme.getNeighbors(nei)) //does it have water next to it
                    {
                        if(world.getFluidState(nei2).isOf(Fluids.WATER)) //its wet => wont work!
                        {
                            canStillBeFine = false; //wet granite is not good
                            break;
                        }
                        else if(world.getBlockState(nei2).isOf(Blocks.COAL_BLOCK)) //if theres a coal block otherwise
                        {
                            //if any neighboring coal block has water next to it, fail definitively.
                            //otherwise, if there is one neighboring (dry) coal block, succeed.
                            otherCoalFound = true;
                            for(BlockPos nei3 : BreakingFemme.getNeighbors(nei2))
                            {
                                if(world.getFluidState(nei3).isOf(Fluids.WATER)) //yes: failed!
                                {
                                    canStillBeFine = false;
                                    break;
                                }
                            }
                        }
                    }
                    if(canStillBeFine && otherCoalFound)
                        graniteFound = true;
                }
            }

            if(!waterFailure && coalFound && graniteFound) //we CAN transform the ruby block!!
                world.setBlockState(tgt, ModBlocks.PARTIALLY_REDUCED_RUBY_BLOCK.getDefaultState()); //then do it! :3
        }
    }
}
