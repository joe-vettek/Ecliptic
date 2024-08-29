package com.teamtea.eclipticseasons.mixin.client.sound;

import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MusicManager.class})
public class MixinMusicManager {
    @Shadow
    private int nextSongDelay;

    @Inject(at = {@At("RETURN")}, method = {"tick"})
    private void ecliptic$init(CallbackInfo ci) {
        nextSongDelay = nextSongDelay;
    }
}
