package com.rhseung.glance

import com.rhseung.glance.network.ClientSyncHandler
import net.fabricmc.api.ClientModInitializer

object ModClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientSyncHandler.registerC2SPayloads();
    }
}