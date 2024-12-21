package com.rhseung.glance.legacy_tooltip.base

import com.rhseung.glance.draw.DrawHelper
import com.rhseung.glance.legacy_tooltip.util.TooltipConstants
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.splitToTwo
import com.rhseung.glance.util.Util.toRangeSize
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent

class SeparatorTooltipComponent(val width: Int, val color: Color) : TooltipComponent {
    override fun getHeight(textRenderer: TextRenderer): Int {
        return TooltipConstants.ITEM_PADDNIG + 1 + TooltipConstants.ITEM_PADDNIG;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return width;
    }

    fun accelerate(value: Float, factor: Float): Float {
        return (value * factor).coerceIn(0f, 1f);
    }

    override fun drawItems(
        textRenderer: TextRenderer,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        val splited = (x.toRangeSize(width)).splitToTwo();

        DrawHelper.drawHorizontalLineInt(
            context,
            splited.first,
            y + TooltipConstants.ITEM_PADDNIG
        ) { color.toInt((accelerate(it, 0.8f) * 255).toInt()) };

        DrawHelper.drawHorizontalLineInt(
            context,
            splited.second.reversed(),
            y + TooltipConstants.ITEM_PADDNIG,
        ) { color.toInt((accelerate(it, 0.8f) * 255).toInt()) };
    }
}