package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.util.Util.toMutableText
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.equipment.trim.ArmorTrimMaterial
import net.minecraft.item.equipment.trim.ArmorTrimPattern
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class ArmorTrimContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    override fun getComponents(): List<LineComponent> {
        val armorTrimComponent = itemStack.get(DataComponentTypes.TRIM)!!;
        val pattern: ArmorTrimPattern = armorTrimComponent.pattern.value();
        val material: ArmorTrimMaterial = armorTrimComponent.material.value();

        val materialName = Text.translatable(material.ingredient.value().translationKey);

        return listOf(LineComponent(
            TextComponent(
                materialName.fillStyle(material.description.style)
                    .append(" ")
                    .append(pattern.description().toMutableText().formatted(Formatting.GRAY))
            )
        ));
    }

    override fun getShiftComponents(): List<LineComponent> {
        return this.getComponents();
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::ArmorTrimContent, ::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return itemStack.contains(DataComponentTypes.TRIM);
        }
    }
}