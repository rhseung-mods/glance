package com.rhseung.glance.tooltip.component

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent

class CenteredLineComponent(vararg components: TooltipComponent) : GlanceTooltipComponent {
    val components = components.toMutableList();

    fun add(component: GlanceTooltipComponent): CenteredLineComponent {
        components.add(component);
        return this;
    }

    fun add(index: Int, component: GlanceTooltipComponent): CenteredLineComponent {
        components.add(index, component);
        return this;
    }

    fun addAll(components: Collection<GlanceTooltipComponent>): CenteredLineComponent {
        this.components.addAll(components);
        return this;
    }

    fun addAll(index: Int, components: Collection<GlanceTooltipComponent>): CenteredLineComponent {
        this.components.addAll(index, components);
        return this;
    }

    fun addAll(lineComponent: CenteredLineComponent): CenteredLineComponent {
        components.addAll(lineComponent.components);
        return this;
    }

    fun addAll(index: Int, lineComponent: CenteredLineComponent): CenteredLineComponent {
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
        y0: Int
    ) {
        var x = x0;
        var y = y0;

        components.forEachIndexed { index, component ->
            val eachHeight = component.getHeight(textRenderer);
            val lineHeight = getHeight(textRenderer);
            val y_ = y + (lineHeight - eachHeight) / 2;

            if (component is CenteredTextComponent) {
                val textWidth = component.getWidth(textRenderer);
                val beforeWidth = x - x0;
                val afterWidth = components.subList(index + 1, components.size).sumOf { it.getWidth(textRenderer) };

                val space = innerWidth - (beforeWidth + afterWidth);
                x += (space - textWidth) / 2;
                component.drawItems(textRenderer, x, y_, innerWidth, innerHeight, context);
                x = innerWidth - afterWidth;
            }
            else {
                component.drawItems(textRenderer, x, y_, innerWidth, innerHeight, context);
                x += component.getWidth(textRenderer);
            }
        }
    }
}