package com.rhseung.glance.network.payload

import com.rhseung.glance.ModMain
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

/**
 * kotlin 2.0.x 버전에서 컴파일하는데 Failed requirement 오류가 발생함. build.gradle 파일에서 2.1.0으로 업그레이드해서 해결
 */
class SaturationSyncPayloadS2C(val saturation: Float) : CustomPayload {
    companion object : ClientPlayNetworking.PlayPayloadHandler<SaturationSyncPayloadS2C> {
        val CODEC: PacketCodec<PacketByteBuf, SaturationSyncPayloadS2C> = CustomPayload.codecOf(SaturationSyncPayloadS2C::write, ::SaturationSyncPayloadS2C);
        val ID = CustomPayload.Id<SaturationSyncPayloadS2C>(ModMain.id("saturation"));

        fun register() {
            PayloadTypeRegistry.playS2C().register(ID, CODEC);
            ClientPlayNetworking.registerGlobalReceiver(ID, ::receive);
        }

        override fun receive(payload: SaturationSyncPayloadS2C, ctx: ClientPlayNetworking.Context) {
            ctx.client().execute {
                ctx.client().player?.hungerManager?.saturationLevel = payload.saturation;
            }
        }
    }

    constructor(buf: PacketByteBuf) : this(buf.readFloat());

    fun write(buf: PacketByteBuf) {
        buf.writeFloat(saturation);
    }

    override fun getId(): CustomPayload.Id<SaturationSyncPayloadS2C> {
        return ID;
    }
}