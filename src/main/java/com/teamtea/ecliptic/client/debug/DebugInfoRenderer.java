package com.teamtea.ecliptic.client.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamtea.ecliptic.api.solar.SolarTerm;
import com.teamtea.ecliptic.common.AllListener;
import com.teamtea.ecliptic.common.core.biome.WeatherManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldDimensions;
import org.lwjgl.opengl.GL11;

public final class DebugInfoRenderer {
    private final Minecraft mc;

    public DebugInfoRenderer(Minecraft mc) {

        this.mc = mc;
    }

    public void renderStatusBar(GuiGraphics matrixStack, int screenWidth, int screenHeight, int solar, long dayTime, double env, int solarTime) {

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        String solarS = "Solar Terms Day: " + solar;
        String dayS = "Day Time: " + dayTime;
        String envS = "Env Temp: " + env;
        String solarTimeS = "Solar Time: " + solarTime;

        int index = 0;

        drawInfo(matrixStack, screenWidth, screenHeight, solarS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, dayS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, envS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, solarTimeS, index++);

        for (Level level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (level.dimension() == Level.OVERWORLD && level instanceof ServerLevel) {
                if (Minecraft.getInstance().cameraEntity instanceof Player player) {
                    var standBiome = level.getBiome(player.getOnPos());
                    for (WeatherManager.BiomeWeather biomeWeather : WeatherManager.getBiomeList(level)) {
                        if (((Holder.Reference<Biome>) biomeWeather.biomeHolder).key().location().equals(((Holder.Reference<Biome>) standBiome).key().location())) {
                            var solarTerm = AllListener.getSaveData(level).getSolarTerm();
                            String solarTermS = "Solar Term: " + solarTerm;
                            String biomeRainS = "Biome Rain: " + solarTerm.getBiomeRain(biomeWeather.biomeHolder);
                            String snowTermS = "Snow Term: " + SolarTerm.getSnowTerm(biomeWeather.biomeHolder.get());
                            drawInfo(matrixStack, screenWidth, screenHeight, "", index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, solarTermS, index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, biomeRainS, index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, snowTermS, index++);

                            drawInfo(matrixStack, screenWidth, screenHeight, "", index++);

                            String rainTimeS = "Rain Time: " + biomeWeather.rainTime;
                            String clearTimeS = "Clear Time: " + biomeWeather.clearTime;
                            String snowDepthS = "Snow Depth: " + biomeWeather.snowDepth;

                            drawInfo(matrixStack, screenWidth, screenHeight, rainTimeS, index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, clearTimeS, index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, snowDepthS, index++);


                            break;
                        }
                    }

                }
            }
        }


        RenderSystem.enableBlend();
        // RenderSystem.disableAlphaTest();
        mc.getTextureManager().bindForSetup(OverlayEventHandler.DEFAULT);

    }

    private void drawInfo(GuiGraphics matrixStack, int screenWidth, int screenHeight, String s, int index) {
        matrixStack.drawString(mc.font, s, screenWidth / 2 - mc.font.width(s) / 2, index * 9 + 3, 0xFFFFFF);
    }
}