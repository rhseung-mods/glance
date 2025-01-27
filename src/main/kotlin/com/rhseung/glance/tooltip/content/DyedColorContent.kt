package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.with
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class DyedColorContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    override fun getComponents(): List<LineComponent> {
        val dyedColorComponent = itemStack.get(DataComponentTypes.DYED_COLOR)!!;
        val color = Color(dyedColorComponent.rgb);

        // translate
        return listOf(LineComponent(
            TextComponent(("Dyed Color: " with Color.GRAY).append(color.toString() with color))
        ));
    }

    override fun getShiftComponents(): List<LineComponent> {
        return this.getComponents();
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::DyedColorContent, ::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return itemStack.contains(DataComponentTypes.DYED_COLOR);
        }
    }
}