package com.rhseung.glance.network

import com.rhseung.glance.network.payload.SaturationSyncPayload
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

object ClientSyncHandler {
    @Environment(EnvType.CLIENT)
    fun registerC2SPayloads() {
        ClientPlayNetworking.registerGlobalReceiver(SaturationSyncPayload.ID) { payload, context ->
            context.client().execute {
                context.client().player?.hungerManager?.saturationLevel = payload.saturation;
            }
        };
    }
}