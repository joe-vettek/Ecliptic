package com.teamtea.ecliptic.api.constant.climate;

import com.teamtea.ecliptic.api.constant.solar.Season;
import com.teamtea.ecliptic.api.constant.solar.SolarTerm;

public enum MonsoonRain implements BiomeRain {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(),
    RAIN_WATER(),
    INSECTS_AWAKENING(),
    SPRING_EQUINOX(),
    FRESH_GREEN(),
    GRAIN_RAIN(),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0.3F),
    LESSER_FULLNESS(0.5F, 0.1f),
    GRAIN_IN_EAR(0.7F, 0.15f),
    SUMMER_SOLSTICE(0.8F, 0.2f),
    LESSER_HEAT(0.95F, 0.15f),
    GREATER_HEAT(0.8F, 0.1f),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0.7F, 0.05F),
    END_OF_HEAT(0.6F, 0.03F),
    WHITE_DEW(0.5F, 0.02F),
    AUTUMNAL_EQUINOX(0.4F, 0.02F),
    COLD_DEW(0.3F, 0.01F),
    FIRST_FROST(0.25F, 0.01F),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(),
    LIGHT_SNOW(),
    HEAVY_SNOW(),
    WINTER_SOLSTICE(),
    LESSER_COLD(),
    GREATER_COLD(),

    NONE(0.0F, 0.0F);

    private final float rainChane;
    private final float thunderChance;

    MonsoonRain() {
        this(0, 0);
    }

    MonsoonRain(float rainChane) {
        this(rainChane, 0);
    }

    MonsoonRain(float rainChane, float thunderChance) {
        this.rainChane = rainChane;
        this.thunderChance = thunderChance;
    }

    public String getName() {
        return this.toString().toLowerCase();
    }

    @Override
    public float getRainChane() {
        return rainChane;
    }
    @Override
    public float getThunderChance() {
        return thunderChance;
    }

    @Override
    public SolarTerm getSolarTerm() {
        return SolarTerm.values()[this.ordinal()];
    }
    @Override
    public Season getSeason() {
        return Season.values()[this.ordinal() / 6];
    }
}
