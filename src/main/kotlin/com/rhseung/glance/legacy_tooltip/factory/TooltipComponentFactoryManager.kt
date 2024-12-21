package com.rhseung.glance.legacy_tooltip.factory

import net.minecraft.client.gui.tooltip.BundleTooltipComponent
import net.minecraft.client.gui.tooltip.ProfilesTooltipComponent
import net.minecraft.client.gui.tooltip.ProfilesTooltipComponent.ProfilesData
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.item.tooltip.BundleTooltipData
import net.minecraft.item.tooltip.TooltipData
import kotlin.reflect.KClass

object TooltipComponentFactoryManager {
    val factories: MutableMap<KClass<out TooltipData>, (TooltipData) -> TooltipComponent> = mutableMapOf(
        BundleTooltipData::class to { BundleTooltipComponent((it as BundleTooltipData).contents) },
        ProfilesData::class to { ProfilesTooltipComponent(it as ProfilesData) },
    );

    inline fun <reified T: TooltipData> set(noinline factory: (T) -> TooltipComponent) {
        factories[T::class] = { factory(it as T) };
    }

    fun of(tooltipData: TooltipData): TooltipComponent {
        val ret = factories[tooltipData::class] ?: throw IllegalArgumentException("No factory for ${tooltipData::class.simpleName}");
        return ret(tooltipData);
    }
}