package com.rhseung.glance.tooltip.content

import com.rhseung.glance.draw.element.GlanceText.Companion.with
import com.rhseung.glance.draw.element.icon.TooltipIcon
import com.rhseung.glance.legacy_tooltip.util.SpecialChar
import com.rhseung.glance.tooltip.component.GlanceTooltipComponent
import com.rhseung.glance.tooltip.component.IconComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.toStringPretty
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class FuelContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    private val burnTick = MinecraftClient.getInstance().world!!.fuelRegistry.getFuelTicks(itemStack);
    private val itemSmeltTick = 200;
    private val burnAmount = burnTick.toFloat() / itemSmeltTick;
    private val burnAmountText = (SpecialChar.MULTIPLY + burnAmount.toStringPretty()) with Color.FUEL;

    override fun getComponents(): List<GlanceTooltipComponent> {
        return listOf(LineComponent(
            IconComponent(TooltipIcon.FUEL), TextComponent(burnAmountText, shift = 1)
        ));
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::FuelContent, FuelContent::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            val client = MinecraftClient.getInstance();
            return client.world?.fuelRegistry?.isFuel(itemStack) == true &&
                    client.currentScreen is AbstractFurnaceScreen<*>;
        }
    }
}