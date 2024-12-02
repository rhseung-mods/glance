package com.rhseung.glance.draw

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

interface Drawable {
    fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int;

    fun getWidth(textRenderer: TextRenderer): Int;

    fun getHeight(textRenderer: TextRenderer): Int;

    operator fun plus(other: Drawable): DrawableLine {
        return DrawableLine(this, other);
    }

    companion object {
        operator fun Drawable.plus(padding: Int): DrawableLine {
            return DrawableLine(this, Padding(padding));
        }

        operator fun Int.plus(drawable: Drawable): DrawableLine {
            return DrawableLine(Padding(this), drawable);
        }

        operator fun Drawable.plus(text: String): DrawableLine {
            return DrawableLine(this, Text(text));
        }

        operator fun String.plus(drawable: Drawable): DrawableLine {
            return DrawableLine(Text(this), drawable);
        }

        operator fun Drawable.plus(line: DrawableLine): DrawableLine {
            return DrawableLine(this, *line.drawables);
        }
    }
}