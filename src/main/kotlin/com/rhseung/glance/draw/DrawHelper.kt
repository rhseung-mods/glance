package com.rhseung.glance.draw

import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Color
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer

object DrawHelper {
    val DOT = ModMain.id("textures/frame/default.png");

    fun drawHorizontalLine(context: DrawContext, x1: Int, x2: Int, y: Int, z: Int, color: Color = Color.WHITE) {
        if (x1 > x2)
            drawHorizontalLine(context, x1, x2, y, z, color);

        for (x in x1..x2) {
            context.drawTexture(RenderLayer::getGuiTextured, DOT, x, y, 0f, 0f, 1, 1, 1, 1, color.toInt(true));
        }
    }

    fun drawHorizontalLine(context: DrawContext, x1: Int, x2: Int, y: Int, z: Int, colorFactory: (Float) -> Color) {
        if (x1 > x2)
            drawHorizontalLine(context, x2, x1, y, z, colorFactory);

        for (x in x1..x2) {
            val ratio = (x - x1).toFloat() / (x2 - x1);
            context.drawTexture(RenderLayer::getGuiTextured, DOT, x, y, 0f, 0f, 1, 1, 1, 1, colorFactory(ratio).toInt(true));
        }
    }

    fun drawVerticalLine(context: DrawContext, y1: Int, y2: Int, x: Int, z: Int, color: Color = Color.WHITE) {
        if (y1 > y2)
            drawVerticalLine(context, y2, y1, x, z, color);

        for (y in y1..y2) {
            context.drawTexture(RenderLayer::getGuiTextured, DOT, x, y, 0f, 0f, 1, 1, 1, 1, color.toInt(true));
        }
    }

    fun drawVerticalLine(context: DrawContext, y1: Int, y2: Int, x: Int, z: Int, colorFactory: (Float) -> Color) {
        if (y1 > y2)
            drawVerticalLine(context, y2, y1, x, z, colorFactory);

        for (y in y1..y2) {
            val ratio = (y - y1).toFloat() / (y2 - y1);
            context.drawTexture(RenderLayer::getGuiTextured, DOT, x, y, 0f, 0f, 1, 1, 1, 1, colorFactory(ratio).toInt(true));
        }
    }

    fun drawBorder(context: DrawContext, x1: Int, x2: Int, y1: Int, y2: Int, z: Int, color: Color = Color.WHITE) {
        if (x1 > x2)
            drawBorder(context, x2, x1, y1, y2, z, color);
        if (y1 > y2)
            drawBorder(context, x1, x2, y2, y1, z, color);

        drawHorizontalLine(context, x1 + 1, x2 - 1, y1, z, color);
        drawHorizontalLine(context, x1 + 1, x2 - 1, y2, z, color);
        drawVerticalLine(context, y1 + 1, y2 - 1, x1, z, color);
        drawVerticalLine(context, y1 + 1, y2 - 1, x2, z, color);
    }

    fun drawBorder(context: DrawContext, x1: Int, x2: Int, y1: Int, y2: Int, z: Int, colorFactory: (Float) -> Color) {
        if (x1 > x2)
            drawBorder(context, x2, x1, y1, y2, z, colorFactory);
        if (y1 > y2)
            drawBorder(context, x1, x2, y2, y1, z, colorFactory);

        drawHorizontalLine(context, x1 + 1, x2 - 1, y1, z, colorFactory(0f));
        drawHorizontalLine(context, x1 + 1, x2 - 1, y2, z, colorFactory(1f));
        drawVerticalLine(context, y1 + 1, y2 - 1, x1, z, colorFactory);
        drawVerticalLine(context, y1 + 1, y2 - 1, x2, z, colorFactory);
    }
}