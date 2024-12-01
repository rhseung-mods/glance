package com.rhseung.glance.tooltip

import com.rhseung.glance.icon.Icon
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
        val burnAmountText = Text.literal("${SpecialChar.MULTIPLY}${burnAmount.toStringPretty()}");
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return if (data.client.currentScreen is AbstractFurnaceScreen<*>)
            Icon.HEIGHT + Draw.LINE_MARGIN;
        else
            0;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return if (data.client.currentScreen is AbstractFurnaceScreen<*>)
            Draw.ICON_START_PADDING + Icon.WIDTH + Draw.SPACE + textRenderer.getWidth(data.burnAmountText);
        else
            0;
    }

    override fun drawItems(
        textRenderer: TextRenderer,
        x0: Int,
        y0: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        var x = x0 + Draw.ICON_START_PADDING;
        if (data.client.currentScreen is AbstractFurnaceScreen<*>) {
            x = TooltipIcon.FUEL.draw(context, x, y0);
            x += Draw.SPACE;
            x = data.burnAmountText.withColor(Color.FUEL.toInt()).draw(context, textRenderer, x, y0);
        }
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<FuelTooltipData>(::FuelTooltip);
            TooltipDataFactoryManager.set<Item> { item, stack, client ->
                if (client.world?.fuelRegistry?.isFuel(stack) == true)
                    FuelTooltipData(item, stack, client);
                else
                    null
            }
        }
    }
}