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

class DurabilityContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    override fun getComponents(): List<LineComponent> {
        val durability = itemStack.maxDamage - itemStack.damage;
        val durabilityText = (durability.toString() with Color.WHITE)
            .append("/" with Color.DARK_GRAY)
            .append(itemStack.maxDamage.toString() with Color.DARK_GRAY);

        val durabilityComponent = LineComponent(
            IconComponent(TooltipIcon.DURABILITY),
            XPaddingComponent(SPACE),
            TextComponent(durabilityText, shift = 1)
        );

        return listOf(durabilityComponent);
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