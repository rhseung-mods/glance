package com.rhseung.glance

import com.rhseung.glance.init.ModTooltips
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object ModMain : ModInitializer {
	const val MOD_ID = "glance";
    val LOGGER = LoggerFactory.getLogger(MOD_ID);

	fun of(path: String) = Identifier.of(MOD_ID, path);

	override fun onInitialize() {
		ModTooltips.register();
	}
}