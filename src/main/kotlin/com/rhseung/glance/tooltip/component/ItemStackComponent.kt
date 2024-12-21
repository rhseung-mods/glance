package com.rhseung.glance.tooltip.component

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack

class ItemStackComponent(val itemStack: ItemStack, val size: Int = 16) : GlanceTooltipComponent {
    val ratio = size.toFloat() / 16;

    override fun getWidth(textRenderer: TextRenderer): Int {
        return size;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return size;
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int
    ) {
        context.matrices.push();
        context.matrices.scale(ratio, ratio, 1f);
        context.matrices.translate(0f, 0f, 400f);
        context.drawItem(itemStack, (x0 / ratio).toInt(), (y0 / ratio).toInt());
        context.matrices.scale(1 / ratio, 1 / ratio, 1f);
        context.matrices.pop();
    }
}