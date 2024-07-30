package com.teamtea.ecliptic.api.constant.climate;

import com.teamtea.ecliptic.api.constant.solar.Season;
import com.teamtea.ecliptic.api.constant.solar.SolarTerm;

public interface BiomeRain {
    int ordinal();

    float rainChane = 0;
    float thunderChance = 0;

    public default float getRainChane() {
        return this.rainChane;
    }

    public default float getThunderChance() {
        return this.thunderChance;
    }

    public default SolarTerm getSolarTerm() {
        return SolarTerm.values()[this.ordinal()];
    }

    public default Season getSeason() {
        return Season.values()[this.ordinal() / 6];
    }
}