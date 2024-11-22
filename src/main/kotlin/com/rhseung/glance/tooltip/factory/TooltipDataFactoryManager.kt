package com.rhseung.glance.tooltip.factory

import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.base.CompoundTooltip
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

object TooltipDataFactoryManager {
    val factories: MutableMap<KClass<out Item>, MutableList<(Item, ItemStack, MinecraftClient) -> AbstractTooltip.AbstractTooltipData?>> = mutableMapOf();

    inline fun <reified T: Item> set(noinline factory: (T, ItemStack, MinecraftClient) -> AbstractTooltip.AbstractTooltipData?) {
        if (T::class !in factories)
            factories[T::class] = mutableListOf();

        factories[T::class]!!.add { item, stack, client -> factory(item as T, stack, client) };
    }

    fun find(item: Item, stack: ItemStack, client: MinecraftClient): CompoundTooltip.CompoundTooltipData {
        val ret = CompoundTooltip.CompoundTooltipData();

        factories.forEach { (clazz, consumers) ->
            if (item::class.isSubclassOf(clazz) || item::class == clazz) {
                consumers.forEach { consumer ->
                    consumer(item, stack, client)?.let { ret.add(it) };
                }
            }
        }

        return ret;
    }
}