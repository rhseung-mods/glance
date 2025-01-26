package com.rhseung.glance.tooltip.component

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent

class StackedComponent(vararg components: TooltipComponent) : GlanceTooltipComponent {
    val components = components.toList();

    override fun getWidth(textRenderer: TextRenderer): Int {
        return components.maxOfOrNull { it.getWidth(textRenderer) } ?: 0;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return components.maxOfOrNull { it.getHeight(textRenderer) } ?: 0;
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int,
        outerX: Int,
        outerY: Int
    ) {
        for (component in components) {
            if (component is GlanceTooltipComponent)
                component.draw(context, textRenderer, innerWidth, innerHeight, x0, y0, outerX, outerY);
            else
                component.drawItems(textRenderer, x0, y0, innerWidth, innerHeight, context);
        }
    }
}