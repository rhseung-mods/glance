package com.rhseung.glance.icon

import com.rhseung.glance.draw.Drawable
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

abstract class Icon(
    open val variants: Int = 1,
    open val width: Int = 9,
    open val height: Int = 9
): Drawable {
    abstract val id: Identifier;

    open fun draw(context: DrawContext, x: Int, y: Int, index: Int = 0): Int {
        context.drawTexture(
            RenderLayer::getGuiTextured, id,
            x,
            y - 1,
            width.toFloat() * index,
            0F,
            width,
            height,
            width * variants,
            height,
        );

        return x + width;
    }

    companion object {
        val WIDTH = 9;
        val HEIGHT = 9;
    }
}