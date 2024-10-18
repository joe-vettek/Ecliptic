package com.teamtea.eclipticseasons.mixin.common.entity.animal.fox;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Fox.class)
public class MixinFox {

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean ecliptic$tick(Level instance, Operation<Boolean> original) {
        return WeatherManager.isThunderAt((ServerLevel) ((Fox)(Object)this).level,((Fox)(Object)this).blockPosition());
    }

}
