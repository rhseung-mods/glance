package com.rhseung.glance.draw

import com.rhseung.glance.util.Vec2D
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

class DrawableLine(vararg val drawables: Drawable) {
    fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Vec2D {
        var x = x0;
        drawables.forEach { x = it.draw(context, renderer, x, y0); }

        assert(x == x0 + getWidth(renderer));
        return Vec2D(x, y0 + getHeight(renderer));
    }

    fun getWidth(textRenderer: TextRenderer): Int {
        return drawables.sumOf { it.getWidth(textRenderer) };
    }

    fun getHeight(textRenderer: TextRenderer): Int {
        return textRenderer.fontHeight;
//        return drawables.maxOf { getHeight(textRenderer) };
    }

    operator fun plus(other: Drawable): DrawableLine {
        return DrawableLine(*drawables, other);
    }

    operator fun plus(string: String): DrawableLine {
        return DrawableLine(*drawables, Text(string));
    }

    operator fun plus(space: Int): DrawableLine {
        return DrawableLine(*drawables, Padding(space));
    }

    operator fun plus(line: DrawableLine): DrawableLine {
        return DrawableLine(*drawables, *line.drawables);
    }

    fun plusAll(other: DrawableLine): DrawableLine {
        return DrawableLine(*drawables, *other.drawables);
    }
}