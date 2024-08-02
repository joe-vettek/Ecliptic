package com.teamtea.ecliptic.client.sound;

import com.teamtea.ecliptic.Ecliptic;
import com.teamtea.ecliptic.api.constant.solar.Season;
import com.teamtea.ecliptic.api.util.SimpleUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.*;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

public class SeasonalBiomeAmbientSoundsHandler implements AmbientSoundHandler {
    private static final int LOOP_SOUND_CROSS_FADE_TIME = 40;
    private static final float SKY_MOOD_RECOVERY_RATE = 0.001F;
    private final LocalPlayer player;
    private final SoundManager soundManager;
    private final BiomeManager biomeManager;
    private final RandomSource random;
    private final Object2ObjectArrayMap<Biome, LoopSoundInstance> loopSounds = new Object2ObjectArrayMap<>();
    private float moodiness;
    @Nullable
    private Biome previousBiome;
    private Season previousSeason;
    private boolean previousIsDay;

    public SeasonalBiomeAmbientSoundsHandler(LocalPlayer localPlayer, SoundManager soundManager, BiomeManager biomeManager) {
        this.random = localPlayer.level().getRandom();
        this.player = localPlayer;
        this.soundManager = soundManager;
        this.biomeManager = biomeManager;
    }

    public float getMoodiness() {
        return this.moodiness;
    }

    public void tick() {
        this.loopSounds.values().removeIf(AbstractTickableSoundInstance::isStopped);

        boolean indoor = (player.level().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition()) < 12);
        for (LoopSoundInstance loopSoundInstance : this.loopSounds.values()) {
            if (indoor)
                loopSoundInstance.fadeOut();
            else
                loopSoundInstance.fadeIn();
        }
        if (indoor)return;

        var biome = this.biomeManager.getNoiseBiomeAtPosition(this.player.getX(), this.player.getY(), this.player.getZ());
        if (biome.value() != this.previousBiome) {
            this.previousBiome = biome.value();
        }

        {
            var season = SimpleUtil.getNowSolarTerm(player.level()).getSeason();
            boolean isDayNow = SimpleUtil.isDay(player.level());
            if (season != this.previousSeason || isDayNow != this.previousIsDay) {
                this.loopSounds.values().forEach(SeasonalBiomeAmbientSoundsHandler.LoopSoundInstance::fadeOut);
                this.previousSeason = season;
                this.previousIsDay = isDayNow;
                this.loopSounds.clear();
            }

            SoundEvent soundEvent = null;
            switch (season) {
                case SPRING -> {
                    if ((biome.is(Biomes.CHERRY_GROVE)|| biome.is(BiomeTags.IS_FOREST) || biome.is(Tags.Biomes.IS_PLAINS)) && !biome.is(Tags.Biomes.IS_COLD)) {
                        soundEvent = Ecliptic.SoundEventsRegistry.spring_forest;
                    }
                }
                case SUMMER -> {
                    // if (player.level().isNight())
                    // 客户端不计算是否为夜晚
                    if (!isDayNow) {
                        if (!(biome.is(BiomeTags.IS_SAVANNA)
                                && !biome.is(Tags.Biomes.IS_CAVE)
                                && !biome.is(Tags.Biomes.IS_DESERT)
                                && !biome.is(BiomeTags.IS_BADLANDS)
                                && !biome.is(Tags.Biomes.IS_PEAK))) {
                            soundEvent = Ecliptic.SoundEventsRegistry.night_river;
                        }
                    } else {
                        if ((biome.is(Biomes.CHERRY_GROVE)||biome.is(BiomeTags.IS_FOREST) || biome.is(Tags.Biomes.IS_PLAINS) || biome.is(BiomeTags.IS_RIVER))) {
                            soundEvent = Ecliptic.SoundEventsRegistry.garden_wind;
                        }
                    }

                }
                case AUTUMN -> {
                    if ((biome.is(Biomes.CHERRY_GROVE)||biome.is(BiomeTags.IS_FOREST))) {
                        soundEvent = Ecliptic.SoundEventsRegistry.windy_leave;
                    }
                }
                case WINTER -> {
                    if (!biome.is(Tags.Biomes.IS_CAVE)) {
                        if (biome.is(Biomes.CHERRY_GROVE)||biome.is(BiomeTags.IS_FOREST)) {
                            soundEvent = Ecliptic.SoundEventsRegistry.winter_forest;
                        } else soundEvent = Ecliptic.SoundEventsRegistry.winter_cold;
                    }
                }
                case NONE -> {
                }
            }
            if (soundEvent != null) {
                SoundEvent finalSoundEvent = soundEvent;
                this.loopSounds.compute(biome.get(), (biome1, loopSoundInstance) -> {
                    if (loopSoundInstance == null) {
                        loopSoundInstance = new LoopSoundInstance(finalSoundEvent);
                        this.soundManager.play(loopSoundInstance);
                    }


                    return loopSoundInstance;
                });
            }
        }
    }

    public static class LoopSoundInstance extends AbstractTickableSoundInstance {
        private int fadeDirection;
        private int fade;

        public LoopSoundInstance(SoundEvent p_119658_) {
            super(p_119658_, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
            this.looping = true;
            this.delay = 0;
            this.volume = 0.5F;
            this.relative = true;
            // this.fade=40;
        }

        public void tick() {
            if (this.fade < 0) {
                this.stop();
            }
            // Ecliptic.logger(this,this.fade);
            this.fade += this.fadeDirection;
            this.volume = Mth.clamp((float) this.fade / 40.0F, 0.0F, 1.0F);

        }

        public void fadeOut() {
            this.fade = Math.min(this.fade, 40);
            if (this.fade >= 0)
                this.fadeDirection = -1;
            else this.fadeDirection = 0;
        }

        public void fadeIn() {
            this.fade = Math.max(0, this.fade);
            if (this.fade < 40)
                this.fadeDirection = 1;
            else this.fadeDirection = 0;
        }
    }
}
