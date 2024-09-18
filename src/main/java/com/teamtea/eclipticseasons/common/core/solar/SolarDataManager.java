package com.teamtea.eclipticseasons.common.core.solar;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.SolarTermsMessage;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SolarDataManager extends SavedData {

    protected int solarTermsDay = (ServerConfig.Season.initialSolarTermIndex.get() - 1) * ServerConfig.Season.lastingDaysOfEachTerm.get();
    protected int solarTermsTicks = 0;

    protected WeakReference<Level> levelWeakReference;


    public SolarDataManager(Level level) {
        levelWeakReference = new WeakReference<>(level);
    }

    public SolarDataManager(Level level, CompoundTag nbt) {
        this(level);
        setSolarTermsDay(nbt.getInt("SolarTermsDay"));
        setSolarTermsTicks(nbt.getInt("SolarTermsTicks"));
        setLevelData(nbt);
    }

    protected void setLevelData(CompoundTag nbt) {
        if (levelWeakReference.get() != null) {
            var listTag = nbt.getList("biomes", Tag.TAG_COMPOUND);
            var biomeWeathers = WeatherManager.getBiomeList(levelWeakReference.get());
            for (int i = 0; i < listTag.size(); i++) {
                var location = listTag.getCompound(i).getString("biome");
                for (WeatherManager.BiomeWeather biomeWeather : biomeWeathers) {
                    if (location.equals(biomeWeather.location.toString())) {
                        biomeWeather.deserializeNBT(listTag.getCompound(i));
                        break;
                    }
                }
            }
        }
    }


    public static SolarDataManager get(ServerLevel serverLevel) {
        DimensionDataStorage storage = serverLevel.getDataStorage();
        return storage.computeIfAbsent(
                new Factory<>(() -> create(serverLevel),
                        ((compoundTag, provider) -> load(serverLevel, compoundTag, provider))),
                EclipticSeasonsApi.MODID);
    }

    private static SolarDataManager load(ServerLevel serverLevel, CompoundTag compoundTag, HolderLookup.Provider provider) {
        return new SolarDataManager(serverLevel, compoundTag);
    }

    private static SolarDataManager create(ServerLevel serverLevel) {
        return new SolarDataManager(serverLevel);
    }


    public void updateTicks(ServerLevel world) {
        solarTermsTicks++;
        int dayTime = Math.toIntExact(world.getDayTime() % 24000);
        if (solarTermsTicks > dayTime + 100) {
            // solarTermsDay++;
            // solarTermsDay %= 24 * ServerConfig.Season.lastingDaysOfEachTerm.get();
            setSolarTermsDay((getSolarTermsDay() + 1));
            setSolarTermsDay((getSolarTermsDay() % (24 * ServerConfig.Season.lastingDaysOfEachTerm.get())));

            BiomeClimateManager.updateTemperature(world, getSolarTerm());
            sendUpdateMessage(world);
        }
        solarTermsTicks = dayTime;

        setDirty();
    }

    public int getSolarTermIndex() {
        return getSolarTermsDay() / ServerConfig.Season.lastingDaysOfEachTerm.get();
    }

    public SolarTerm getSolarTerm() {
        return SolarTerm.get(this.getSolarTermIndex());
    }

    public int getSolarTermsDay() {
        return solarTermsDay;
    }

    public int getSolarTermsTicks() {
        return solarTermsTicks;
    }

    public void setSolarTermsDay(int solarTermsDay) {
        this.solarTermsDay = Math.max(solarTermsDay, 0) % (24 * ServerConfig.Season.lastingDaysOfEachTerm.get());
        setDirty();
    }

    public void setSolarTermsTicks(int solarTermsTicks) {
        this.solarTermsTicks = solarTermsTicks;
        setDirty();
    }

    public void sendUpdateMessage(ServerLevel world) {
        for (ServerPlayer player : world.players()) {
            SimpleNetworkHandler.send(player, new SolarTermsMessage(this.getSolarTermsDay()));
            if (getSolarTermsDay() % ServerConfig.Season.lastingDaysOfEachTerm.get() == 0) {
                player.sendSystemMessage(Component.translatable("info.eclipticseasons.environment.solar_term.message", SolarTerm.get(getSolarTermIndex()).getAlternationText()), false);
            }
            WeatherManager.tickPlayerForSeasonCheck(player);
        }
    }


    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider pRegistries) {
        compound.putInt("SolarTermsDay", getSolarTermsDay());
        compound.putInt("SolarTermsTicks", getSolarTermsTicks());
        ListTag listTag = new ListTag();
        if (levelWeakReference.get() != null) {
            var list = WeatherManager.getBiomeList(levelWeakReference.get());
            for (WeatherManager.BiomeWeather biomeWeather : list) {
                listTag.add(biomeWeather.serializeNBT());
            }
        }
        compound.put("biomes", listTag);
        return compound;
    }
}
