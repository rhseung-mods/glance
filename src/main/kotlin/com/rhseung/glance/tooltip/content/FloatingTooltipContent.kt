package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.component.LineComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

abstract class FloatingTooltipContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    override fun getShiftComponents(): List<LineComponent> {
        return this.getComponents();
    }
}