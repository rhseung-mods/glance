package com.rhseung.glance.network.payload

import com.rhseung.glance.ModMain
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload

/**
 * kotlin 2.0.x 버전에서 컴파일하는데 Failed requirement 오류가 발생함. build.gradle 파일에서 2.1.0으로 업그레이드해서 해결
 */
class SaturationSyncPayload(val saturation: Float) : CustomPayload {
    companion object {
        val CODEC: PacketCodec<PacketByteBuf, SaturationSyncPayload> = CustomPayload.codecOf(SaturationSyncPayload::write, ::SaturationSyncPayload);
        val ID = CustomPayload.Id<SaturationSyncPayload>(ModMain.id("saturation"));
    }

    constructor(buf: PacketByteBuf) : this(buf.readFloat());

    fun write(buf: PacketByteBuf) {
        buf.writeFloat(saturation);
    }

    override fun getId(): CustomPayload.Id<SaturationSyncPayload> {
        return ID;
    }
}