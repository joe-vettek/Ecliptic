package com.teamtea.ecliptic.mixin.common.bee;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.ecliptic.common.core.biome.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeehiveBlockEntity.class)
public class MixinBeehiveBlockEntity {

    @ModifyExpressionValue(
            method = "releaseOccupant",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")
    )
    private static boolean releaseOccupantCheckRain(boolean original, @Local(ordinal = 0) Level level, @Local(ordinal = 0) BlockPos blockPos  ) {
        return WeatherManager.isRainingAt(blockPos, (ServerLevel) level);
    }

}
