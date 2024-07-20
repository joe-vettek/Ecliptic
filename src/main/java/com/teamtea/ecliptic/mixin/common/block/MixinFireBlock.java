package com.teamtea.ecliptic.mixin.common.block;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.ecliptic.common.core.biome.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireBlock.class)
public class MixinFireBlock {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z", ordinal = 0)
    )
    private boolean mixin_Tick(boolean original, @Local(ordinal = 0) ServerLevel level, @Local(ordinal = 0) BlockPos blockPos) {
        return WeatherManager.isRainingAt(blockPos, level);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z", ordinal = 1)
    )
    private boolean mixin_Tick2(boolean original, @Local(ordinal = 0) ServerLevel level, @Local(ordinal = 0) BlockPos.MutableBlockPos blockPos) {
        return WeatherManager.isRainingAt(blockPos, level);
    }

}