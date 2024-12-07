package com.rhseung.glance.tooltip

import com.rhseung.glance.draw.DrawableTooltip
import com.rhseung.glance.draw.element.Padding
import com.rhseung.glance.draw.element.GlanceText.Companion.with
import com.rhseung.glance.draw.element.icon.TooltipIcon
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.tooltip.util.SpecialChar
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.toStringPretty
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class FuelTooltip(data: FuelTooltipData) : AbstractTooltip<FuelTooltip.FuelTooltipData>(data) {
    class FuelTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) : AbstractTooltipData(item, stack, client) {
        val burnTick = client.world!!.fuelRegistry.getFuelTicks(stack);
        val itemSmeltTick = 200;
        val burnAmount = burnTick.toFloat() / itemSmeltTick;
        val burnAmountText = (SpecialChar.MULTIPLY + burnAmount.toStringPretty()) with Color.FUEL;

        override fun getTooltip(): DrawableTooltip {
            return DrawableTooltip(TooltipIcon.FUEL + Padding.SPACE + burnAmountText);
        }
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<FuelTooltipData>(::FuelTooltip);
            TooltipDataFactoryManager.set<Item> { item, stack, client ->
                if (client.world?.fuelRegistry?.isFuel(stack) == true && client.currentScreen is AbstractFurnaceScreen<*>)
                    FuelTooltipData(item, stack, client);
                else
                    null
            }
        }
    }
}