package com.rhseung.glance.tooltip.component

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent

interface GlanceTooltipComponent : TooltipComponent {
    override fun getWidth(textRenderer: TextRenderer): Int;
    override fun getHeight(textRenderer: TextRenderer): Int;

    fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int,
    );

    override fun drawItems(
        textRenderer: TextRenderer,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        draw(context, textRenderer, width, height, x, y);
    }
}