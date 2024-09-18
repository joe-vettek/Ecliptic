package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.teamtea.eclipticseasons.common.network.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.NetworkdUtil;
import com.teamtea.eclipticseasons.compat.distanthorizons.DHTool;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetworkdUtil.class})
public abstract class MixinNetworkdUtil {

    @Inject(at = {@At("HEAD")}, method = {"processEmptyMessage2"})
    private static void ecliptic$processEmptyMessage2(EmptyMessage emptyMessage, IPayloadContext context, CallbackInfo ci) {
        context.enqueueWork(() -> {
            if (ClientConfig.Renderer.enhancementChunkRenderUpdate.get() && ClientConfig.Renderer.forceChunkRenderUpdate.get()) {
                DHTool.forceReloadAll();
            } else {
                DHTool.clearRenderCache();
            }
        });
    }
}
