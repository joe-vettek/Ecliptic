package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.misc.BasicWeather;
import com.teamtea.eclipticseasons.client.sound.SeasonalBiomeAmbientSoundsHandler;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.server.commands.TickCommand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin({LocalPlayer.class})
public abstract class MixinLocalPlayer implements BasicWeather {

    @Shadow
    @Final
    private List<AmbientSoundHandler> ambientSoundHandlers;

    @Inject(at = {@At("RETURN")}, method = {"<init>"})
    private void ecliptic$init(CallbackInfo ci, @Local Minecraft minecraft, @Local ClientLevel clientLevel) {
        if (ClientConfig.Sound.sound.get() && clientLevel.dimensionType().natural())
            ambientSoundHandlers.add(new SeasonalBiomeAmbientSoundsHandler((LocalPlayer) (Object) this, minecraft.getSoundManager(), clientLevel.getBiomeManager()));
    }
}
