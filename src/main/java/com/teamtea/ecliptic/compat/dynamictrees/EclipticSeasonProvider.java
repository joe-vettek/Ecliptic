package com.teamtea.ecliptic.compat.dynamictrees;

import com.teamtea.ecliptic.common.AllListener;
import com.teamtea.ecliptic.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class EclipticSeasonProvider implements com.ferreusveritas.dynamictrees.compat.season.SeasonProvider {

    private float seasonValue = 1.0f;

    @Override
    public Float getSeasonValue(Level level, BlockPos pos) {
        return seasonValue;
    }

    @Override
    public void updateTick(Level level, long dayTime) {
        var solarDataManager = AllListener.getSaveDataLazy(level);
        if (solarDataManager.resolve().isPresent()) {
            seasonValue = solarDataManager.resolve().get().getSolarTermsDay() / (6f * ServerConfig.Season.lastingDaysOfEachTerm.get());
        }
    }

    @Override
    public boolean shouldSnowMelt(Level level, BlockPos pos) {
        return false;
    }
}
