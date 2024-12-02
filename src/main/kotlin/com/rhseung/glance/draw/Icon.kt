package com.rhseung.glance.draw

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

abstract class Icon(
    open val variants: Int = 1,
    open val width: Int = 9,
    open val height: Int = 9
): Drawable {
    abstract val id: Identifier;

    private var index: Int = 0;

    operator fun get(index: Int): Icon {
        this.index = index;
        return this;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return width;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return height;
    }

    override fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int {
        context.drawTexture(RenderLayer::getGuiTextured, id,
            x0, y0 - 1, (width * index).toFloat(), 0f,
            width, height, width * variants, height
        );

        return x0 + getWidth(renderer);
    }

    operator fun plus(line: DrawableLine): DrawableLine {
        return DrawableLine(this, *line.drawables);
    }
}