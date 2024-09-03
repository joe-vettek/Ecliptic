package com.teamtea.eclipticseasons.mixin.common.entity.monster;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Monster.class)
public class MixinMonster {


    @WrapOperation(
            method = "isDarkEnoughToSpawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z")
    )
    private static boolean ecliptic$isDarkEnoughToSpawn_isThundering(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 0) BlockPos blockPos) {
        if (ServerConfig.Debug.useSolarWeather.get())
            return WeatherManager.isThunderAt(serverLevel, blockPos);
        else return original.call(serverLevel);
    }
}
