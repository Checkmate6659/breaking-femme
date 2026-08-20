package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

//read !!mixinoverride on fabricord to show why this is necessary (in short, mod compat)
//the regular method (simple mixin into EntityModel + instanceof check) would NOT work, because of the @Unique feature variables in VillagerResemblingModel
//so we use this submixin to conditionally modify the (grand)parent (VillagerResemblingMixin)
@Mixin(EntityModel.class)
abstract class DummyFeaturesMixin {
    //define a dummy handler in a mixin to the superclass, that does nothing, but is protected
    @WrapMethod(method = "animateModel")
    protected void breakingfemme$animateModel(Entity entity, float limbAngle, float limbDistance, float tickDelta, Operation<Void> original) {
        original.call(entity, limbAngle, limbDistance, tickDelta);
    }
}
