package com.rhseung.glance.tooltip.icon

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

abstract class Icon(
    open val variants: Int = 1,
    open val width: Int = 9,
    open val height: Int = 9,
    val index: Int = 0,
) {
    abstract val id: Identifier;

    open fun getWidth(textRenderer: TextRenderer): Int {
        return width;
    }

    open fun getHeight(textRenderer: TextRenderer): Int {
        return height;
    }

    abstract operator fun get(index: Int): Icon;

    open fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int {
        if (index < 0 || index >= variants)
            throw Error("index=$index is not valid");

        context.drawTexture(
            RenderLayer::getGuiTextured, id,
            x0, y0, (width * index).toFloat(), 0f,
            width, height, width * variants, height
        );

        return x0 + getWidth(renderer);
    }
}