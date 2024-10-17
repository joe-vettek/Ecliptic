package com.teamtea.eclipticseasons.client;

import com.teamtea.eclipticseasons.EclipticSeasonsMod;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.particle.ButterflyParticle;
import com.teamtea.eclipticseasons.client.particle.FallenLeavesParticle;
import com.teamtea.eclipticseasons.client.particle.FireflyParticle;
import com.teamtea.eclipticseasons.client.particle.WildGooseParticle;
import com.teamtea.eclipticseasons.client.render.ber.CalendarBlockEntityRenderer;
import com.teamtea.eclipticseasons.client.render.ber.PaperWindmillBlockEntityRenderer;
import com.teamtea.eclipticseasons.client.render.ber.WindChimesBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.*;

import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

import java.awt.*;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    // does the Glass Lantern render in the given layer (RenderType) - used as Predicate<RenderType> lambda for setRenderLayer
    public static boolean isGlassLanternValidLayer(RenderType layerToCheck) {
        return layerToCheck == RenderType.cutoutMipped() || layerToCheck == RenderType.translucent();
    }

    @SubscribeEvent
    public static void blockRegister(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(EclipticSeasonsMod.ParticleRegistry.FIREFLY, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new FireflyParticle(level, x, y, z, p_277215_));
        event.registerSpriteSet(EclipticSeasonsMod.ParticleRegistry.WILD_GOOSE, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new WildGooseParticle(level, x, y, z,0.01,0.01,0.01, p_277215_));
        event.registerSpriteSet(EclipticSeasonsMod.ParticleRegistry.BUTTERFLY, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new ButterflyParticle(level, x, y, z, p_277215_));
        event.registerSpriteSet(EclipticSeasonsMod.ParticleRegistry.FALLEN_LEAVES, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new FallenLeavesParticle(level, x, y, z, p_277222_, p_277223_, p_277224_,particleType, p_277215_));



    }

    @SubscribeEvent
    public static void registerExtraModels(ModelEvent.RegisterAdditional event) {
        // Minecraft.getInstance().getResourceManager().listPacks().toList().get(0).getResource(PackType.CLIENT_RESOURCES, ResourceLocation.withDefaultNamespace("textures/block/snow.png")).get()
        // IOUtils.toString(Minecraft.getInstance().getResourceManager().listPacks().toList().get(0).getResource(PackType.SERVER_DATA, ResourceLocation.withDefaultNamespace("recipe/yellow_terracotta.json")).get(), StandardCharsets.UTF_8)        event.register(ModelManager.snowy_fern);
        event.register(ModelManager.snowy_fern);
        event.register(ModelManager.snowy_grass);
        event.register(ModelManager.snowy_tall_grass_top);
        event.register(ModelManager.snowy_tall_grass_bottom);
        event.register(ModelManager.snowy_large_fern_top);
        // 注意这里使用地址和model地址效果不同，后者需要写blockstate
        event.register(ModelManager.snowy_large_fern_bottom);
        event.register(ModelManager.snowy_dandelion);
        event.register(ModelManager.dandelion_top);
        event.register(ModelManager.overlay_2);
        event.register(ModelManager.snow_height2);
        event.register(ModelManager.snow_height2_top);
        event.register(ModelManager.grass_flower);
        for (ModelResourceLocation flowerOnGrass : ModelManager.flower_on_grass) {
            event.register(flowerOnGrass);
        }
    }

    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event) {
        EclipticSeasonsMod.logger("Register Client");
        event.enqueueWork(() -> {
            // ItemBlockRenderTypes.setRenderLayer(ModContents.fluiddrawer.get(), ClientSetup::isGlassLanternValidLayer);
            // MenuScreens.register(ModContents.containerType.get(), Screen.Slot1::new);
            //
            ItemBlockRenderTypes.setRenderLayer(Blocks.BAMBOO_BLOCK, RenderType.cutoutMipped());
            // ItemBlockRenderTypes.setRenderLayer(ModContents.RiceSeedlingBlock.get(),RenderType.cutout());
            // fix json file instead
            BiomeColors.GRASS_COLOR_RESOLVER = BiomeColorsHandler.GRASS_COLOR;
            BiomeColors.FOLIAGE_COLOR_RESOLVER = BiomeColorsHandler.FOLIAGE_COLOR;
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EclipticSeasonsMod.ModContents.calendar_entity_type.get(), CalendarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(EclipticSeasonsMod.ModContents.paper_wind_mill_entity_type.get(), PaperWindmillBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(EclipticSeasonsMod.ModContents.wind_chimes_entity_type.get(), WindChimesBlockEntityRenderer::new);
    }

    // public static Map<ResourceLocation, BakedModel> BakedSnowModels=new HashMap<>();

    @SubscribeEvent
    public static void onModelBaked(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
        ModelManager.models = modelRegistry;
        // snowModel.resolve();

        ModelManager.quadMap.clear();
        ModelManager.quadMap_1.clear();
    }

    @SubscribeEvent
    public static void onRegisterColorHandlersEvent_Block(RegisterColorHandlersEvent.Block event) {
        // BlockState birchLeaves = Blocks.BIRCH_LEAVES.defaultBlockState();
        // BlockColors blockColors = event.getBlockColors();

        event.register((state, blockAndTintGetter, pos, i) -> {
            if (i == 1) {
                return blockAndTintGetter != null && pos != null ? BiomeColors.getAverageGrassColor(blockAndTintGetter, pos) : GrassColor.getDefaultColor();
            } else {
                return -1;
            }
        }, Blocks.DANDELION);

        event.register(BiomeColorsHandler::getSpruceColor, Blocks.SPRUCE_LEAVES);
        event.register(BiomeColorsHandler::getBirchColor, Blocks.BIRCH_LEAVES);
        event.register(BiomeColorsHandler::getMangroveColor, Blocks.MANGROVE_LEAVES);
    }

    @SubscribeEvent
    public static void onRegisterColorHandlersEvent_Item(RegisterColorHandlersEvent.Item event) {
    }

    @SubscribeEvent
    public static void onRegisterShader(RegisterShadersEvent event) {

    }
}
