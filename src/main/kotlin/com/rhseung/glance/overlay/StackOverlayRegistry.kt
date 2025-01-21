package com.rhseung.glance.overlay

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import kotlin.collections.plusAssign

object StackOverlayRegistry {
    private val registry: MutableList<(Item, ItemStack) -> GlanceStackOverlay> = mutableListOf();
    private val validators: MutableList<(Item, ItemStack) -> Boolean> = mutableListOf();

    fun <T: GlanceStackOverlay> register(
        content: (Item, ItemStack) -> T,
        validator: (Item, ItemStack) -> Boolean
    ) {
        registry += content;
        validators += validator;
    }

    fun find(item: Item, itemStack: ItemStack): List<GlanceStackOverlay> {
        return registry.zip(validators)
            .filter { (_, validator) -> validator(item, itemStack) }
            .map { (content, _) -> content(item, itemStack) };
    }

    fun register() {
        CooldownProgressOverlay.register();
        HoneyLevelOverlay.register();
        ItemBarOverlay.register();
        StackCountOverlay.register();
    }
}