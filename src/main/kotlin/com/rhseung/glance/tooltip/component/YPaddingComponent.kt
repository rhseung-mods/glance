package com.rhseung.glance.tooltip.component

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

class YPaddingComponent(val size: Int) : GlanceTooltipComponent {
    override fun getHeight(textRenderer: TextRenderer): Int {
        return size;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return 0;
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int
    ) {}
}