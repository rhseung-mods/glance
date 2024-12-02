package com.rhseung.glance.tooltip

import com.rhseung.glance.draw.DrawableGroup
import com.rhseung.glance.draw.Icon
import com.rhseung.glance.draw.Padding
import com.rhseung.glance.draw.Text.Companion.with
import com.rhseung.glance.icon.TooltipIcon
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.util.SpecialChar
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Draw
import com.rhseung.glance.util.Draw.draw
import com.rhseung.glance.util.Util.toStringPretty
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class FuelTooltip(override val data: FuelTooltipData) : AbstractTooltip(data) {
    class FuelTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) : AbstractTooltipData(item, stack, client) {
        val burnTick = client.world!!.fuelRegistry.getFuelTicks(stack);
        val itemSmeltTick = 200;
        val burnAmount = burnTick.toFloat() / itemSmeltTick;
        val burnAmountText = (SpecialChar.MULTIPLY + burnAmount.toStringPretty()) with Color.FUEL;

        fun getTooltip(): DrawableGroup {
            return DrawableGroup(Padding.ICON_START + TooltipIcon.FUEL + Padding.SPACE + burnAmountText);
        }
    }

    val tooltip = data.getTooltip();

    override fun getHeight(textRenderer: TextRenderer): Int {
        return tooltip.getHeight(textRenderer);
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return tooltip.getWidth(textRenderer);
    }

    override fun drawItems(
        textRenderer: TextRenderer,
        x0: Int,
        y0: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        tooltip.draw(context, textRenderer, x0, y0);
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