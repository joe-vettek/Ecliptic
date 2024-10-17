package com.teamtea.eclipticseasons.data.model;

import com.teamtea.eclipticseasons.EclipticSeasonsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ESItemModelProvider extends ItemModelProvider {


    public static final String GENERATED = "item/generated";
    public static final String HANDHELD = "item/handheld";

    public ESItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    private String blockName(BlockItem blockItem) {
        return BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).getPath();
    }

    @Override
    protected void registerModels() {
        addSimple(EclipticSeasonsMod.ModContents.calendar_item.value());
        addSimple(EclipticSeasonsMod.ModContents.wind_chimes_item.value());
        addSimple(EclipticSeasonsMod.ModContents.paper_wind_chimes_item.value());
        addSimple(EclipticSeasonsMod.ModContents.bamboo_wind_chimes_item.value());

        withExistingParent(itemName(EclipticSeasonsMod.ModContents.paper_wind_mill_item.value()), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", ResourceLocation.withDefaultNamespace("item/"+itemName(Items.STICK)));
        withExistingParent(itemName(EclipticSeasonsMod.ModContents.broom_item.value()), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", ResourceLocation.withDefaultNamespace("item/"+itemName(Items.STICK)));
        withExistingParent(itemName(EclipticSeasonsMod.ModContents.snowy_maker_item.value()), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", ResourceLocation.withDefaultNamespace("item/"+itemName(Items.STICK)));
    }

    public void addSimple(Item item) {
        withExistingParent(itemName(item), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", resourceItem(itemName(item)));
    }

    private String itemName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    public ResourceLocation resourceItem(String path) {
        return EclipticSeasonsMod.rl("item/" + path);
    }


}
