package com.teamtea.eclipticseasons.api.constant.solar.color;


public enum NoneSolarTermColors implements SolarTermColor {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(),
    RAIN_WATER(),
    INSECTS_AWAKENING(),
    SPRING_EQUINOX(),
    FRESH_GREEN(),
    GRAIN_RAIN(),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(),
    LESSER_FULLNESS(),
    GRAIN_IN_EAR(),
    SUMMER_SOLSTICE(),
    LESSER_HEAT(),
    GREATER_HEAT(),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(),
    END_OF_HEAT(),
    WHITE_DEW(),
    AUTUMNAL_EQUINOX(),
    COLD_DEW(),
    FIRST_FROST(),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(),
    LIGHT_SNOW(),
    HEAVY_SNOW(),
    WINTER_SOLSTICE(),
    LESSER_COLD(),
    GREATER_COLD();


    private final float ratio;


    NoneSolarTermColors(float ratio) {
        this.ratio = ratio;
    }

    NoneSolarTermColors() {
        this.ratio = 0;
    }


    public static NoneSolarTermColors get(int index) {
        return values()[index];
    }

    @Override
    public int getColor() {
        return TemperateSolarTermColors.get(this.ordinal()).getColor();
    }

    @Override
    public float getMix() {
        return this.ratio;
    }

    @Override
    public int getBirchColor() {
        return TemperateSolarTermColors.get(this.ordinal()).getBirchColor();
    }
}
