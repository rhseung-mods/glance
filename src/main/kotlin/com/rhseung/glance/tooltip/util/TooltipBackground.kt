package com.rhseung.glance.tooltip.util

import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Color
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

object TooltipBackground {
    val BACKGROUND_TEXTURE = Identifier.ofVanilla("tooltip/background");
    val FRAME_TEXTURE = Identifier.ofVanilla("tooltip/frame");

    fun render(context: DrawContext, x: Int, y: Int, width: Int, height: Int, z: Int, color: Color) {
        val i = x - 3 - 9;
        val j = y - 3 - 9;
        val k = width + 3 + 3 + 18;
        val l = height + 3 + 3 + 18;

        context.matrices.push();
        context.matrices.translate(0.0f, 0.0f, z.toFloat());
        context.drawGuiTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, i, j, k, l);
        context.drawGuiTexture(RenderLayer::getGuiTextured, FRAME_TEXTURE, i, j, k, l, color.toInt(true));
        context.matrices.pop();
    }
}