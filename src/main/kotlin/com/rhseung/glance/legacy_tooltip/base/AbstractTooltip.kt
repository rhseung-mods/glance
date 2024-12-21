package com.rhseung.glance.legacy_tooltip.base

import com.rhseung.glance.draw.DrawableTooltip
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipData

abstract class AbstractTooltip<T: AbstractTooltip.AbstractTooltipData>(data: T, val canVanish: Boolean = true) : TooltipComponent {
    abstract class AbstractTooltipData(open val item: Item, open val stack: ItemStack, open val client: MinecraftClient) : TooltipData {
        abstract fun getTooltip(): DrawableTooltip;
    }

    val tooltip: DrawableTooltip = data.getTooltip();

    override fun getHeight(textRenderer: TextRenderer): Int {
        return tooltip.getHeight(textRenderer);
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return tooltip.getWidth(textRenderer);
    }

    override fun drawItems(textRenderer: TextRenderer, x0: Int, y0: Int, width: Int, height: Int, context: DrawContext) {
        tooltip.draw(context, textRenderer, x0, y0);
    }
}