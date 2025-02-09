package com.rhseung.glance.tooltip.template

import com.rhseung.glance.tooltip.TooltipDecor
import com.rhseung.glance.tooltip.component.GlanceTooltipComponent
import com.rhseung.glance.tooltip.component.TextComponent
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent

abstract class GlanceTooltip(
    open val titles: List<TextComponent>,
    open val components: List<TooltipComponent>,
    open val floatingComponents: List<GlanceTooltipComponent>,
    open val theme: TooltipDecor.Theme
) {
    protected abstract var tooltip: MutableList<TooltipComponent>;

    fun getWidth(textRenderer: TextRenderer): Int {
        return tooltip.maxOfOrNull { it.getWidth(textRenderer) } ?: 0;
    }

    fun getHeight(textRenderer: TextRenderer): Int {
        return tooltip.sumOf { it.getHeight(textRenderer) };
    }

    fun drawInner(context: DrawContext, textRenderer: TextRenderer, x0: Int, y0: Int, outerX: Int, outerY: Int) {
        var x = x0;
        var y = y0;

        for (component in tooltip) {
            if (component is GlanceTooltipComponent)
                component.draw(context, textRenderer, getWidth(textRenderer), getHeight(textRenderer), x, y, outerX, outerY);
            else
                component.drawItems(textRenderer, x, y, getWidth(textRenderer), getHeight(textRenderer), context);
            y += component.getHeight(textRenderer);
        }

        x = outerX;
        y = outerY - 2 * floatingComponents.size - floatingComponents.sumOf { it.getHeight(textRenderer) };

        for (component in floatingComponents) {
            component.draw(context, textRenderer, getWidth(textRenderer), getHeight(textRenderer), x, y, outerX, outerY);
            y += component.getHeight(textRenderer) + 2;
        }
    }
}