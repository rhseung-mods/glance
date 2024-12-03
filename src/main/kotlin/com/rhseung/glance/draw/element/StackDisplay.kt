package com.rhseung.glance.draw.element

import com.rhseung.glance.draw.Drawable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack

class StackDisplay(val stack: ItemStack, private val size: Int = 16) : Drawable {
    val ratio = size.toFloat() / 16;

    override fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int {
        context.matrices.scale(ratio, ratio, 1f);
        context.drawItem(stack, (x0 / ratio).toInt(), (y0 / ratio).toInt());
        context.matrices.scale(1 / ratio, 1 / ratio, 1f);
        return x0 + getWidth(renderer);
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return size;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return size;
    }
}