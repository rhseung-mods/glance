package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.component.ContainerComponent
import com.rhseung.glance.tooltip.component.LineComponent
import net.minecraft.block.ShulkerBoxBlock
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.DyedColorComponent
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class ContainerContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    override fun getComponents(): List<LineComponent> {
        val containerComponent = itemStack.get(DataComponentTypes.CONTAINER)!!;
        val color = if (item is BlockItem && item.block is ShulkerBoxBlock)
            (item.block as ShulkerBoxBlock).color?.entityColor ?: -1;
        else
            DyedColorComponent.getColor(itemStack, -1);

        // todo: bundle
        return listOf(LineComponent(
            ContainerComponent(
                containerComponent.stream().toList(),
                3,
                9,
                color
            )   // todo: size registry, mod support (how?)
        ));
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::ContainerContent, ::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return itemStack.contains(DataComponentTypes.CONTAINER) &&
                    itemStack.get(DataComponentTypes.CONTAINER)!!.stream().toList().isNotEmpty();
        }
    }
}