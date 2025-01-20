package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.MapComponent
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.MapIdComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

class MapContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    val mapId: MapIdComponent = itemStack.get(DataComponentTypes.MAP_ID)!!;

    override fun getComponents(): List<LineComponent> {
        return listOf(LineComponent(MapComponent(mapId)));
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::MapContent, MapContent::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return itemStack.contains(DataComponentTypes.MAP_ID);
        }
    }
}