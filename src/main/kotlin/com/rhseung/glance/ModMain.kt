package com.rhseung.glance

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
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object ModMain : ModInitializer {
	const val MOD_ID = "glance";
    val LOGGER = LoggerFactory.getLogger(MOD_ID);

	fun id(path: String): Identifier = Identifier.of(MOD_ID, path);

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