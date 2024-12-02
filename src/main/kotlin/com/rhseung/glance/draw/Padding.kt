package com.rhseung.glance.draw

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

data class Padding(val size: Int) : Drawable {
    companion object {
        val BETWEEN_SLOT_ATTR = Padding(12);
        val SPACE = Padding(3);
        val BETWEEN_SIGN_VALUE = Padding(2);
        val LINE_MARGIN = Padding(2);
        val SLOT_MARGIN = Padding(12);
        val NEXT_ICON = Padding(8);
        val ICON_START = Padding(1);
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return size;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return textRenderer.fontHeight;
    }

    override fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int {
        return x0 + getWidth(renderer);
    }

    operator fun plus(line: DrawableLine): DrawableLine {
        return DrawableLine(this, *line.drawables);
    }

    operator fun plus(string: String): DrawableLine {
        return DrawableLine(this, Text(string));
    }
}