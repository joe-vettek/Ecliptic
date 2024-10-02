package com.teamtea.eclipticseasons.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static final ModConfigSpec CLIENT_CONFIG = new ModConfigSpec.Builder().configure(ClientConfig::new).getRight();

    protected ClientConfig(ModConfigSpec.Builder builder) {
        GUI.load(builder);
        Renderer.load(builder);
        Sound.load(builder);
        Particle.load(builder);
    }

    public static class GUI {
        public static ModConfigSpec.BooleanValue debugInfo;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("GUI");
            debugInfo = builder.comment("Info used for development.")
                    .define("DebugInfo", false);
            builder.pop();
        }
    }

    public static class Renderer {
        public static ModConfigSpec.BooleanValue forceChunkRenderUpdate;
        public static ModConfigSpec.BooleanValue enhancementChunkRenderUpdate;

        public static ModConfigSpec.BooleanValue useVanillaCheck;
        public static ModConfigSpec.BooleanValue snowyWinter;
        public static ModConfigSpec.BooleanValue flowerOnGrass;
        public static ModConfigSpec.BooleanValue deeperSnow;
        public static ModConfigSpec.IntValue weatherBufferDistance;
        public static ModConfigSpec.BooleanValue seasonalColorWorld;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Renderer");
            forceChunkRenderUpdate = builder.comment("Force to update chunk rendering.")
                    .define("ForceChunkRenderUpdate", true);
            enhancementChunkRenderUpdate = builder.comment("Enhanced reload, which will refresh all sections periodically.")
                    .define("EnhancementChunkRenderUpdate", false);

            useVanillaCheck = builder.comment("Determines whether snow is falling based on vanilla lighting checks.")
                    .define("useVanillaCheck", false);
            snowyWinter = builder.comment("If snow falls during cold weather, it will gradually cover all solid blocks and grass.")
                    .define("SnowyWinter", true);
            deeperSnow = builder.comment("Occasionally a thicker layer of snow will cover the flowers and grass, especially.")
                    .define("DeeperSnow", false);
            weatherBufferDistance = builder.comment("Modify the buffer distance for local weather changes.")
                    .defineInRange("WeatherBufferDistance", 16, 1, 80);
            seasonalColorWorld = builder.comment("The colors of the grass and leaves change with the time of year.")
                    .define("SeasonalColorWorld", true);
            flowerOnGrass = builder.comment("In spring, grass blocks will occasionally have small flowers on them.")
                    .define("FlowerOnGrass", true);
            builder.pop();
        }
    }

    public static class Sound {
        public static ModConfigSpec.BooleanValue naturalSound;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Sound");
            naturalSound = builder.comment("Listen to the sounds of nature.")
                    .define("NaturalSound", true);
            builder.pop();
        }
    }


    public static class Particle {
        public static ModConfigSpec.BooleanValue seasonParticle;

        public static ModConfigSpec.BooleanValue butterfly;
        public static ModConfigSpec.IntValue butterflySpawnWeight;
        public static ModConfigSpec.BooleanValue fallenLeaves;
        public static ModConfigSpec.IntValue fallenLeavesDropWeight;
        public static ModConfigSpec.BooleanValue firefly;
        public static ModConfigSpec.IntValue fireflySpawnWeight;
        public static ModConfigSpec.BooleanValue wildGoose;
        public static ModConfigSpec.IntValue wildGooseSpawnWeight;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Particle");
            seasonParticle = builder.comment("See butterflies in the spring, fireflies in the summer, and fallen leaves.")
                    .define("SeasonParticle", true);
            butterfly = builder.comment("In spring, butterflies fly over the flowers.")
                    .define("Butterfly", true);
            butterflySpawnWeight = builder.comment("The difficulty multiplier of butterfly particles, the value should be between 1-10000, the default is 10.")
                    .defineInRange("butterflySpawnWeight", 10, 1, 10000);

            fallenLeaves = builder.comment("Leaf blocks will drop leaves, and most frequently in the fall.")
                    .define("FallenLeaves", true);
            fallenLeavesDropWeight = builder.comment("The difficulty multiplier of fallen leaves particles, the value should be between 1-10000, the default is 10.")
                    .defineInRange("FallenLeavesDropWeight", 10, 1, 10000);

            firefly = builder.comment("In the summer evenings, you can see fireflies beside the flowers.")
                    .define("Firefly", true);
            fireflySpawnWeight = builder.comment("The difficulty multiplier of firefly particles, the value should be between 1-10000, the default is 10.")
                    .defineInRange("FireflySpawnWeight", 10, 1, 10000);

            wildGoose = builder.comment("When the grass and trees turn yellow, the wild geese fly south.")
                    .define("WildGoose", true);
            wildGooseSpawnWeight = builder.comment("The difficulty multiplier of wild geese particles, the value should be between 1-10000, the default is 10.")
                    .defineInRange("WildGooseSpawnWeight", 10, 1, 10000);

            builder.pop();
        }
    }
}
