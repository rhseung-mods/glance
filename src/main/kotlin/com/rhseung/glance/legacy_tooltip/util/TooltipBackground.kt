package com.rhseung.glance.legacy_tooltip.util

import com.rhseung.glance.ModMain
import com.rhseung.glance.draw.DrawHelper
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.modify
import net.minecraft.client.gui.DrawContext

object TooltipBackground {

    val BACKGROUND_TEXTURE = ModMain.id("tooltip/background");
    val FRAME_TEXTURE = ModMain.id("tooltip/frame");

    fun render(context: DrawContext, xRange: IntRange, yRange: IntRange, z: Int, color: Color, borderColor: (Float) -> Color = { color.darker(it * 0.6f) }) {
//        context.matrices.push();
        DrawHelper.drawSprite(context, BACKGROUND_TEXTURE, xRange, yRange, z, color);
        DrawHelper.drawBorder(context, xRange.modify(1, -1), yRange.modify(1, -1), z, borderColor);
//        context.matrices.pop();

//        val i = x - 3 - 9;
//        val j = y - 3 - 9;
//        val k = width + 3 + 3 + 18;
//        val l = height + 3 + 3 + 18;
//
//        context.matrices.push();
//        context.matrices.translate(0.0f, 0.0f, z.toFloat());
//        context.drawGuiTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, i, j, k, l, color.toInt(true));
//        DrawHelper.drawBorder(context, x - 3, x + width - 2, y - 3, y + height - 2, z, borderColor);
//        context.matrices.pop();
    }

    fun renderSimple(context: DrawContext, xRange: IntRange, yRange: IntRange, z: Int, color: Color, borderColor: Color = color.darker(0.6f)) {
//        context.matrices.push();
        DrawHelper.drawSprite(context, BACKGROUND_TEXTURE, xRange, yRange, z, color);
        DrawHelper.drawBorder(context, xRange.modify(1, -1), yRange.modify(1, -1), z, borderColor);
//        context.matrices.pop();

//        val i = x - 3 - 9;
//        val j = y - 3 - 9;
//        val k = width + 3 + 3 + 18;
//        val l = height + 3 + 3 + 18;
//
//        context.matrices.push();
//        context.matrices.translate(0.0f, 0.0f, z.toFloat());
//        context.drawGuiTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, i, j, k, l, color.toInt(true));
//        DrawHelper.drawBorder(context, x - 3, x + width + 2, y - 3, y + height + 2, z, borderColor);
//        context.matrices.pop();
    }
}