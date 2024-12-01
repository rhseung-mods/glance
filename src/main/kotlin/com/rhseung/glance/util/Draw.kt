package com.rhseung.glance.util

import com.rhseung.glance.util.Util.ifElse
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object Draw {
    const val BETWEEN_SIGN_VALUE = 2;
    const val SPACE = 3;
    const val LINE_MARGIN = 2;
    const val NEXT_ICON_MARGIN = 8;
    const val SLOT_MARGIN = 12;
    const val ICON_START_PADDING = 1;

    fun String.draw(
        context: DrawContext,
        renderer: TextRenderer,
        x: Int,
        y: Int,
        color: Color = Color.WHITE,
    ): Int {
        context.drawText(renderer, this, x, y, color.toInt(), true);
        return x + renderer.getWidth(this);
    }

    fun Text.draw(
        context: DrawContext,
        renderer: TextRenderer,
        x: Int,
        y: Int,
    ): Int {
        context.drawText(renderer, this, x, y, -1, true);
        return x + renderer.getWidth(this);
    }

    fun List<Pair<String, Color>>.toText(): Text {
        val ret = Text.empty();
        this.forEach { (str, color) ->
            ret.append(Text.literal(str).withColor(color.toInt()));
        }
        return ret;
    }
}