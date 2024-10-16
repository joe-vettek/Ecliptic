package com.teamtea.eclipticseasons.mixin.common.entity.animal.bee;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.util.WeatherUtil;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Bee.BeePollinateGoal.class)
public class MixinBee_BeePollinateGoal {

    // @Shadow @Final private Bee this$0;


   @Dynamic
   @Shadow @Final private Bee this$0;

    @WrapOperation(
            method = "canBeeUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")
    )
    private boolean ecliptic$canBeeUseCheckRain(Level instance, Operation<Boolean> original) {
        return WeatherUtil.isEntityInRain(this$0);
    }


    @WrapOperation(
            method = "canBeeContinueToUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")
    )
    private boolean ecliptic$canBeeContinueToUseCheckRain(Level instance, Operation<Boolean> original) {
        return WeatherUtil.isEntityInRain(this$0);
    }

}
