package com.teamtea.ecliptic.api;

import com.teamtea.ecliptic.Ecliptic;
import com.teamtea.ecliptic.common.core.biome.WeatherManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EclipticBiomeTags {

    public static final TagKey<Biome> SEASONAL = create("seasonal");
    public static final TagKey<Biome> MONSOONAL = create("monsoonal");

    public static final TagKey<Biome> RAINLESS = create("rainless");
    public static final TagKey<Biome> ARID = create("arid");
    public static final TagKey<Biome> DROUGHTY = create("droughty");
    public static final TagKey<Biome> SOFT = create("soft");
    public static final TagKey<Biome> RAINY = create("rainy");

    public static final List<TagKey<Biome>> BIOMES = List.of(SEASONAL, MONSOONAL, RAINLESS, ARID, DROUGHTY, SOFT, RAINY);

    private static TagKey<Biome> create(String s) {
        return TagKey.create(Registries.BIOME, Ecliptic.rl(s));
    }


    public static final Map<Biome, TagKey<Biome>> BIOME_TAG_KEY_MAP = new HashMap<>(128);


    public static TagKey<Biome> getTag(Biome biome) {
        return getTag(WeatherManager.getMainServerLevel(), biome);
    }

    public static TagKey<Biome> getTag(Level level, Biome biome) {
        var bt = BIOME_TAG_KEY_MAP.getOrDefault(biome, null);

        if (bt == null && level != null) {
            var biomes = level.registryAccess().registry(Registries.BIOME);
            if (biomes.isPresent()) {
                for (Map.Entry<ResourceKey<Biome>, Biome> resourceKeyBiomeEntry : biomes.get().entrySet()) {
                    if (resourceKeyBiomeEntry.getValue() == biome) {
                        var holder = biomes.get().getHolder(resourceKeyBiomeEntry.getKey());
                        if (holder.isPresent()) {
                            var tag = holder.get().tags().filter(BIOMES::contains).findFirst();
                            if (tag.isPresent()) {
                                bt = tag.get();
                            }
                        }
                    }
                }
            }
        }
        if (bt == null)
            bt = RAINLESS;
        BIOME_TAG_KEY_MAP.put(biome, bt);
        return bt;
    }
}
