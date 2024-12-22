package com.rhseung.glance.tooltip.component

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent

class ShiftedComponent(val component: TooltipComponent, val dx: Int = 0, val dy: Int = 0) : GlanceTooltipComponent {
    override fun getWidth(textRenderer: TextRenderer): Int {
        return component.getWidth(textRenderer);
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return component.getHeight(textRenderer);
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int
    ) {
        component.drawItems(textRenderer, x0 + dx, y0 + dy, innerWidth, innerHeight, context);
    }
}