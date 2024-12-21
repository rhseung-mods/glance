package com.rhseung.glance.tooltip.component

import com.rhseung.glance.util.Icon
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

class IconComponent(val icon: Icon) : GlanceTooltipComponent {
    override fun getWidth(textRenderer: TextRenderer): Int {
        return icon.width;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return icon.height;
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int
    ) {
        icon.draw(context, textRenderer, x0, y0);
    }
}