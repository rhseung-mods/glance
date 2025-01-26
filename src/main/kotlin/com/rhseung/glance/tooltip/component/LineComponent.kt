package com.rhseung.glance.tooltip.component

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent

class LineComponent(vararg components: TooltipComponent) : GlanceTooltipComponent {
    val components = components.toMutableList();

    fun add(component: GlanceTooltipComponent): LineComponent {
        components.add(component);
        return this;
    }

    fun add(index: Int, component: GlanceTooltipComponent): LineComponent {
        components.add(index, component);
        return this;
    }

    fun addAll(components: Collection<GlanceTooltipComponent>): LineComponent {
        this.components.addAll(components);
        return this;
    }

    fun addAll(index: Int, components: Collection<GlanceTooltipComponent>): LineComponent {
        this.components.addAll(index, components);
        return this;
    }

    fun addAll(lineComponent: LineComponent): LineComponent {
        components.addAll(lineComponent.components);
        return this;
    }

    fun addAll(index: Int, lineComponent: LineComponent): LineComponent {
        components.addAll(index, lineComponent.components);
        return this;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return components.sumOf { it.getWidth(textRenderer) };
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
        var x = x0;
        var y = y0;

        components.forEachIndexed { index, component ->
            if (component is CenteredTextComponent) {
                val textWidth = component.getWidth(textRenderer);
                val beforeWidth = x - x0;
                val afterWidth = components.subList(index + 1, components.size).sumOf { it.getWidth(textRenderer) };

                val space = innerWidth - (beforeWidth + afterWidth);
                x += (space - textWidth) / 2;
                component.draw(context, textRenderer, innerWidth, innerHeight, x, y, outerX, outerY);
                x = innerWidth - afterWidth;
            }
            else if (component is GlanceTooltipComponent) {
                component.draw(context, textRenderer, innerWidth, innerHeight, x, y, outerX, outerY);
                x += component.getWidth(textRenderer);
            } else {
                component.drawItems(textRenderer, x, y, innerWidth, innerHeight, context);
                x += component.getWidth(textRenderer);
            }
        }
    }
}