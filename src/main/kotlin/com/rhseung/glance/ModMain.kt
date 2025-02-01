package com.rhseung.glance

import com.mojang.serialization.Codec
import com.rhseung.glance.network.ClientSyncHandler
import com.rhseung.glance.network.ServerSyncHandler
import com.rhseung.glance.overlay.StackOverlayRegistry
import com.rhseung.glance.test.GlanceTestItems
import com.rhseung.glance.tooltip.content.DurabilityContent
import com.rhseung.glance.tooltip.content.AttributeContent
import com.rhseung.glance.tooltip.content.EnchantmentContent
import com.rhseung.glance.tooltip.content.FoodContent
import com.rhseung.glance.tooltip.content.FuelContent
import com.rhseung.glance.tooltip.content.MapContent
import com.rhseung.glance.tooltip.content.TooltipContentRegistry
import net.fabricmc.api.ModInitializer
import net.minecraft.component.ComponentType
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object ModMain : ModInitializer {
	const val MOD_ID = "glance";
    val LOGGER = LoggerFactory.getLogger(MOD_ID);

	fun id(path: String): Identifier = Identifier.of(MOD_ID, path);

	val SELECTED_INDEX: ComponentType<Int> = Registry.register(
		Registries.DATA_COMPONENT_TYPE,
		id("selected_index"),
		ComponentType.builder<Int>()
			.codec(Codec.INT)
			.packetCodec(PacketCodecs.INTEGER)
			.build()
	);

	const val SELECTED_INDEX_DEFAULT = -1;

	override fun onInitialize() {
		// tooltip content
		TooltipContentRegistry.register();

		// stack overlay
		StackOverlayRegistry.register();

		// network
		ClientSyncHandler.registerC2SPayloads();

		// test
		GlanceTestItems.init();
	}
}