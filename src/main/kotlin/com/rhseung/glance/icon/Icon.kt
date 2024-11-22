package com.rhseung.glance.icon

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

abstract class Icon(open val variants: Int = 1) {
    abstract val id: Identifier;

    fun draw(context: DrawContext, x: Int, y: Int, index: Int = 0): Int {
        context.drawTexture(
            RenderLayer::getGuiTextured, id,
            x,
            y - 1,
            WIDTH.toFloat() * index,
            0F,
            WIDTH,
            HEIGHT,
            WIDTH * variants,
            HEIGHT,
        );

        return x + WIDTH;
    }

    companion object {
        val WIDTH = 9;
        val HEIGHT = 9;
    }
}