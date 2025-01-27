package com.rhseung.glance.tooltip.content

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

object TooltipContentRegistry {
    private val registry: MutableList<(Item, ItemStack) -> GlanceTooltipContent> = mutableListOf();
    private val validators: MutableList<(Item, ItemStack) -> Boolean> = mutableListOf();

    fun <T: GlanceTooltipContent> register(
        content: (Item, ItemStack) -> T,
        validator: (Item, ItemStack) -> Boolean
    ) {
        registry += content;
        validators += validator;
    }

    fun find(item: Item, itemStack: ItemStack): List<GlanceTooltipContent> {
        return registry.zip(validators)
            .filter { (_, validator) -> validator(item, itemStack) }
            .map { (content, _) -> content(item, itemStack) };
    }

    /**
     * 순서 중요함.
     */
    fun register() {
        BundleContent.register();
        ContainerContent.register();
        MapContent.register();

        AttributeContent.register();
        FoodContent.register();
        ArmorTrimContent.register();
        EnchantmentContent.register();
        DyedColorContent.register();
        FuelContent.register();
        DurabilityContent.register();
        StatusEffectContent.register();
        BeehiveContent.register();
    }
}