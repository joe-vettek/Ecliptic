package com.teamtea.eclipticseasons.common.network;


import com.teamtea.eclipticseasons.EclipticSeasonsMod;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

@EventBusSubscriber(modid = EclipticSeasonsApi.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class SimpleNetworkHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar(EclipticSeasonsMod.NETWORK_VERSION);

        registrar.playToClient(
                SolarTermsMessage.TYPE,
                SolarTermsMessage.STREAM_CODEC,
                NetworkdUtil::processSolarTermsMessage2
        );


        registrar.playToClient(
                EmptyMessage.TYPE,
                EmptyMessage.STREAM_CODEC,
                NetworkdUtil::processEmptyMessage2
        );

        registrar.playToClient(
                BiomeWeatherMessage.TYPE,
                BiomeWeatherMessage.STREAM_CODEC,
                NetworkdUtil::processBiomeWeatherMessage2
        );

        registrar.playToClient(
                ChunkUpdateMessage.TYPE,
                ChunkUpdateMessage.STREAM_CODEC,
                NetworkdUtil::processChunkUpdateMessage
        );
    }


    public static <MSG extends CustomPacketPayload> void send(ServerPlayer player, MSG msg) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    public static <MSG extends CustomPacketPayload> void send(List<ServerPlayer> players, MSG msg) {
        players.forEach(player -> {
            // if (player instanceof ServerPlayer serverPlayer)
            {
                send(player, msg);
            }
        });
    }
}
