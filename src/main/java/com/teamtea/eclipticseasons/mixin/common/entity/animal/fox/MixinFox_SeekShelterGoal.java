package com.teamtea.eclipticseasons.mixin.common.entity.animal.fox;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Fox.SeekShelterGoal.class)
public class MixinFox_SeekShelterGoal {

    // @Shadow @Final private Bee this$0;


    @Shadow
    @Final
    private Fox this$0;

    @WrapOperation(
            method = "canUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean ecliptic$canUse_Thunder(Level instance, Operation<Boolean> original) {
        return WeatherManager.isThunderAt((ServerLevel) (this$0).level(), this$0.blockPosition());
    }

}
