package com.rhseung.glance.tooltip.component

import com.rhseung.glance.tooltip.TooltipDecor
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

class BoxComponent(
    val width: Int,
    val height: Int,
    val theme: TooltipDecor.Theme
) : GlanceTooltipComponent {

    override fun getWidth(textRenderer: TextRenderer): Int {
        return width + 2;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return height + 2;
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int
    ) {
        val innerX = x0 + 1;
        val innerY = y0 + 1;

        context.matrices.push();

        // bg
        context.fill(innerX, innerY, innerX + width, innerY + height, 400, theme.topOfBackground.toInt(200));
        // top
        context.fill(innerX, innerY - 1, innerX + width, innerY, 400, theme.topOfOutline.toInt(150));
        // bottom
        context.fill(innerX, innerY + height, innerX + width, innerY + height + 1, 400, theme.topOfOutline.toInt(150));
        // left
        context.fill(innerX - 1, innerY, innerX, innerY + height, 400, theme.topOfOutline.toInt(150));
        // right
        context.fill(innerX + width, innerY, innerX + width + 1, innerY + height, 400, theme.topOfOutline.toInt(150));

        context.matrices.pop();
    }
}