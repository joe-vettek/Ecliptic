package com.teamtea.eclipticseasons.data.tag;

import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TagsDataProvider extends TagsProvider<Biome> {


    public TagsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.BIOME, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ClimateTypeBiomeTags.SEASONAL).addTags(Tags.Biomes.IS_OVERWORLD);
        this.tag(ClimateTypeBiomeTags.MONSOONAL).addTags(Tags.Biomes.IS_SAVANNA);
        this.tag(ClimateTypeBiomeTags.RAINLESS).addTags(Tags.Biomes.IS_CAVE);
        this.tag(ClimateTypeBiomeTags.ARID).addTags(Tags.Biomes.IS_BADLANDS, Tags.Biomes.IS_DESERT);
        this.tag(ClimateTypeBiomeTags.DROUGHTY).addTags(Tags.Biomes.IS_MOUNTAIN_PEAK, Tags.Biomes.IS_COLD_OVERWORLD);
        this.tag(ClimateTypeBiomeTags.SOFT).addTags(Tags.Biomes.IS_BEACH, Tags.Biomes.IS_OCEAN, Tags.Biomes.IS_DEEP_OCEAN);
        this.tag(ClimateTypeBiomeTags.RAINY).addTags(Tags.Biomes.IS_JUNGLE);
        this.tag(ClimateTypeBiomeTags.IS_SMALL).addTags(Tags.Biomes.IS_BEACH, Tags.Biomes.IS_RIVER);
    }
}
