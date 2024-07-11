package com.teamtea.ecliptic.api.climate;

import com.teamtea.ecliptic.api.solar.Season;
import com.teamtea.ecliptic.api.solar.SolarTerm;
import org.apache.commons.lang3.NotImplementedException;

public enum FlatRain implements BiomeRain {

    RAINLESS(),
    ARID(0.01F),
    DROUGHTY(0.1F, 0.001f),
    SOFT(0.4F, 0.005f),
    RAINY(1.0F, 0.01f),
    NONE();

    private final float rainChane;
    private final float thunderChance;

    FlatRain() {
        this(0, 0);
    }

    FlatRain(float rainChane) {
        this(rainChane, 0);
    }


    FlatRain(float rainChane, float thunderChance) {
        this.rainChane = rainChane;
        this.thunderChance = thunderChance;
    }


    @Override
    public float getRainChane() {
        return this.rainChane;
    }

    @Override
    public float getThunderChance() {
        return this.thunderChance;
    }

    @Override
    public SolarTerm getSolarTerm() {
        throw new NotImplementedException("Flat Biome");
    }

    @Override
    public Season getSeason() {
        throw new NotImplementedException("Flat Biome");
    }

}
