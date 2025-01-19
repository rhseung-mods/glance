package com.rhseung.glance.network

import com.rhseung.glance.network.payload.SaturationSyncPayloadS2C
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

object ServerSyncHandler {
    fun registerS2CPayloads() {
        SaturationSyncPayloadS2C.register();
    }

    val saturationByPlayer = mutableMapOf<UUID, Float>();

    fun onPlayerUpdate(player: ServerPlayerEntity) {
        val lastSaturation = saturationByPlayer[player.uuid];
        val saturation = player.hungerManager.saturationLevel;

        if (lastSaturation == null || lastSaturation != saturation) {
            ServerPlayNetworking.send(player, SaturationSyncPayloadS2C(saturation));
            saturationByPlayer[player.uuid] = saturation;
        }
    }

    fun onPlayerConnect(player: ServerPlayerEntity) {
        saturationByPlayer.remove(player.uuid);
    }
}