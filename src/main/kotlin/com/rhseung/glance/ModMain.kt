package com.rhseung.glance

import com.rhseung.glance.draw.DrawHelper
import com.rhseung.glance.tooltip.*
import com.rhseung.glance.tooltip.base.CompoundTooltip
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object ModMain : ModInitializer {
	const val MOD_ID = "glance";
    val LOGGER = LoggerFactory.getLogger(MOD_ID);

	fun id(path: String): Identifier = Identifier.of(MOD_ID, path);

	override fun onInitialize() {
		CompoundTooltip.register();
		AttributeTooltip.register();
		FoodTooltip.register();
		FuelTooltip.register();
		EnchantedBookTooltip.register();
		ArmorModelTooltip.register();
	}
}