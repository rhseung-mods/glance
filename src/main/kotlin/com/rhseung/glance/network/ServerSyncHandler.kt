package com.rhseung.glance.network

import com.rhseung.glance.network.payload.SaturationSyncPayload
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

object ServerSyncHandler {
    fun registerS2CPayloads() {
        PayloadTypeRegistry.playS2C().register(SaturationSyncPayload.ID, SaturationSyncPayload.CODEC);
    }

    val saturationByPlayer = mutableMapOf<UUID, Float>();

    fun onPlayerUpdate(player: ServerPlayerEntity) {
        val lastSaturation = saturationByPlayer[player.uuid];
        val saturation = player.hungerManager.saturationLevel;

        if (lastSaturation == null || lastSaturation != saturation) {
            ServerPlayNetworking.send(player, SaturationSyncPayload(saturation));
            saturationByPlayer[player.uuid] = saturation;
        }
    }

    fun onPlayerConnect(player: ServerPlayerEntity) {
        saturationByPlayer.remove(player.uuid);
    }
}