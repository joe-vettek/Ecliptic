package com.teamtea.eclipticseasons.mixin.game;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.util.WeatherUtil;
import com.teamtea.eclipticseasons.config.ServerConfig;
import com.teamtea.eclipticseasons.game.AnimalHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeehiveBlockEntity.class)
public class MixinBeehiveBlockEntity {

    @Inject(at = {@At("HEAD")}, method = {"releaseOccupant"}, cancellable = true)
    private static void ecliptic$releaseOccupant(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) Level level, @Local(ordinal = 0) BlockPos blockPos) {
        if (AnimalHooks.cancelBeeOut(level, blockPos)) {
            cir.setReturnValue(false);
        }
    }
}
