package com.rhseung.glance.tooltip.base

import com.rhseung.glance.draw.DrawHelper
import com.rhseung.glance.tooltip.util.TooltipConstants
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

    override fun drawItems(
        textRenderer: TextRenderer,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        val splited = (x.toRangeSize(width)).splitToTwo();

        DrawHelper.drawHorizontalLine(
            context,
            splited.first,
            y + TooltipConstants.ITEM_PADDNIG
        ) { color.darker(1 - it) };

        DrawHelper.drawHorizontalLine(
            context,
            splited.second,
            y + TooltipConstants.ITEM_PADDNIG,
        ) { color.darker(it) };
    }
}