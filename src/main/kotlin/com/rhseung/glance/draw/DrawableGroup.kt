package com.rhseung.glance.draw

import com.rhseung.glance.util.Vec2D
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

class DrawableGroup(vararg val lines: DrawableLine) {
    fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Vec2D {
        var cursor = Vec2D(x0, y0);

        lines.forEach { line ->
            cursor = line.draw(context, renderer, cursor.x, cursor.y);
            cursor.x = x0;
            cursor.y += Padding.LINE_MARGIN.size;
        }

        return cursor;
    }

    fun getWidth(textRenderer: TextRenderer): Int {
        return lines.maxOf { it.getWidth(textRenderer) };
    }

    fun getHeight(textRenderer: TextRenderer): Int {
        return lines.sumOf { it.getHeight(textRenderer) } + Padding.LINE_MARGIN.size * lines.size;
    }

    operator fun plus(other: DrawableLine): DrawableGroup {
        return DrawableGroup(*lines, other);
    }
}