package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.TooltipConstants.Padding.SPACE
import com.rhseung.glance.tooltip.component.IconComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.tooltip.component.XPaddingComponent
import com.rhseung.glance.tooltip.icon.TooltipIcon
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.with
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.AnvilScreen
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText

class DurabilityContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    val durability = itemStack.maxDamage - itemStack.damage;
    val durabilityText: MutableText = (durability.toString() with Color.WHITE)
        .append(("/" + itemStack.maxDamage.toString()) with Color.GRAY);

    override fun getComponents(): List<LineComponent> {
        return listOf(LineComponent(
            IconComponent(TooltipIcon.DURABILITY),
            XPaddingComponent(SPACE),
            TextComponent(durabilityText, shiftY = 1)
        ));
    }

    override fun getShiftComponents(): List<LineComponent> {
        // translate: "내구도: "
        return listOf(LineComponent(
            TextComponent(("Durability: " with Color.GRAY).append(durabilityText))
        ));
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::DurabilityContent, DurabilityContent::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            val currentScreen = MinecraftClient.getInstance().currentScreen;
            return (currentScreen is AnvilScreen || currentScreen is GrindstoneScreen) && itemStack.isDamageable;
        }
    }
}