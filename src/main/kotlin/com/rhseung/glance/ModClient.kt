package com.rhseung.glance

import com.rhseung.glance.event.RenderTickEvents
import com.rhseung.glance.network.ClientSyncHandler
import com.rhseung.glance.tooltip.component.ArmorStand3DComponent
import net.fabricmc.api.ClientModInitializer

object ModClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientSyncHandler.registerC2SPayloads();

        // event
        RenderTickEvents.EVENT.register(object : RenderTickEvents {
            override fun tick() {
                ArmorStand3DComponent.updateTimer();
            }
        });
    }
}