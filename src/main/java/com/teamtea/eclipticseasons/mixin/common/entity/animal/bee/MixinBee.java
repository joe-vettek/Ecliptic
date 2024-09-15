package com.teamtea.eclipticseasons.mixin.common.entity.animal.bee;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.util.WeatherUtil;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Bee.class)
public class MixinBee {

    @WrapOperation(
            method = "wantsToEnterHive",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")
    )
    private boolean ecliptic$wantsToEnterHiveCheckRain(Level level, Operation<Boolean> original) {
        if (ServerConfig.Debug.useSolarWeather.get())
            return WeatherUtil.isEntityInRainOrSnow((Bee) (Object) this);
        else return original.call(level);
    }

}
