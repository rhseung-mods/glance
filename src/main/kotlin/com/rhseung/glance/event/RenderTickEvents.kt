package com.rhseung.glance.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

interface RenderTickEvents {
    fun tick();

    companion object {
        val EVENT: Event<RenderTickEvents> = EventFactory.createArrayBacked(RenderTickEvents::class.java) { listeners ->
            object : RenderTickEvents {
                override fun tick() {
                    listeners.forEach(RenderTickEvents::tick);
                }
            }
        }
    }
}