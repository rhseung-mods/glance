package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.TooltipConstants
import com.rhseung.glance.tooltip.component.IconComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.with
import com.rhseung.glance.util.Util.toStringPretty
import com.rhseung.glance.tooltip.icon.TooltipIcon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class FuelContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    private val burnTick = MinecraftClient.getInstance().world!!.fuelRegistry.getFuelTicks(itemStack);
    private val itemSmeltTick = 200;
    private val burnAmount: Float = burnTick.toFloat() / itemSmeltTick;
    private val burnAmountText = (TooltipConstants.Char.MULTIPLY + burnAmount.toStringPretty()) with Color.FUEL;

    override fun getComponents(): List<LineComponent> {
        return listOf(LineComponent(
            IconComponent(TooltipIcon.FUEL), TextComponent(burnAmountText, shiftY = 1)
        ));
    }

    override fun getShiftComponents(): List<LineComponent> {
        // translate: "연소: "
        return listOf(LineComponent(
            TextComponent(("Burn " with Color.GRAY)
                .append((burnAmount.toStringPretty() + "x") with Color.WHITE)
                .append(" Item${if (burnAmount == 1f) "" else "s"}" with Color.GRAY)
            )
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