package com.rhseung.glance

import com.rhseung.glance.event.RenderTickEvents
import com.rhseung.glance.network.ClientSyncHandler
import com.rhseung.glance.tooltip.component.Armor3DBoxComponent
import net.fabricmc.api.ClientModInitializer

object ModClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientSyncHandler.registerC2SPayloads();

        // event
        RenderTickEvents.EVENT.register(object : RenderTickEvents {
            override fun tick() {
                Armor3DBoxComponent.updateTimer();
            }
        });
    }
}