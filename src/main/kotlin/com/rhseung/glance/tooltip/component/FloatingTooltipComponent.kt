package com.rhseung.glance.tooltip.component

import net.minecraft.client.font.TextRenderer

interface FloatingTooltipComponent : GlanceTooltipComponent {
    override fun getWidth(textRenderer: TextRenderer): Int = 0;
    override fun getHeight(textRenderer: TextRenderer): Int = 0;

    fun getWidthExact(textRenderer: TextRenderer): Int;
    fun getHeightExact(textRenderer: TextRenderer): Int;
}