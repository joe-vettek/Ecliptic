package com.teamtea.eclipticseasons.mixin.common.entity.animal.bee;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.util.WeatherUtil;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Bee.BeePollinateGoal.class)
public class MixinBee_BeePollinateGoal {

    // @Shadow @Final private Bee this$0;


    @Shadow
    @Final
    private Bee this$0;

    @WrapOperation(
            method = "canBeeUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")
    )
    private boolean ecliptic$canBeeUseCheckRain(Level level, Operation<Boolean> original) {
        if (ServerConfig.Debug.useSolarWeather.get())
            return WeatherUtil.isEntityInRainOrSnow(this$0);
        else return original.call(level);

    }


    @WrapOperation(
            method = "canBeeContinueToUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")
    )
    private boolean ecliptic$canBeeContinueToUseCheckRain(Level level, Operation<Boolean> original) {
        if (ServerConfig.Debug.useSolarWeather.get())
            return WeatherUtil.isEntityInRainOrSnow(this$0);
        else return original.call(level);
    }

}
