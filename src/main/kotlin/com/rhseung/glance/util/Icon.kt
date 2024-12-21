package com.rhseung.glance.util

import com.rhseung.glance.draw.Drawable
import com.rhseung.glance.draw.DrawableLine
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

abstract class Icon(
    open val variants: Int = 1,
    open val width: Int = 9,
    open val height: Int = 9,
    val index: Int = 0,
): Drawable {
    abstract val id: Identifier;

    override fun getWidth(textRenderer: TextRenderer): Int {
        return width;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return height;
    }

    abstract operator fun get(index: Int): Icon;

    override fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int {
        if (index < 0 || index >= variants)
            throw Error("index=$index is not valid");

        context.drawTexture(RenderLayer::getGuiTextured, id,
            x0, y0, (width * index).toFloat(), 0f,
            width, height, width * variants, height
        );

        return x0 + getWidth(renderer);
    }

    operator fun plus(line: DrawableLine): DrawableLine {
        return DrawableLine(this, *line.drawables);
    }
}