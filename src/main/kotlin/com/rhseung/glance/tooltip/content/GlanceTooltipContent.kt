package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.component.LineComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

abstract class GlanceTooltipContent(open val item: Item, open val itemStack: ItemStack) {
    abstract fun getComponents(): List<LineComponent>;

    abstract fun getShiftComponents(): List<LineComponent>;

    interface Factory {
        fun register();

        fun valid(item: Item, itemStack: ItemStack): Boolean;
    }
}