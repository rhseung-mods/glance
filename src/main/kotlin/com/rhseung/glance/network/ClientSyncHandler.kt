package com.rhseung.glance.network

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

object ClientSyncHandler {
    @Environment(EnvType.CLIENT)
    fun registerC2SPayloads() {}
}