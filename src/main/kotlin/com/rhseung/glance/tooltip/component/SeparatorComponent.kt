package com.rhseung.glance.tooltip.component

import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.getProperty
import com.rhseung.glance.util.Util.splitToTwo
import com.rhseung.glance.util.Util.toRangeSize
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider

class SeparatorComponent(val color: Color) : GlanceTooltipComponent {
    override fun getWidth(textRenderer: TextRenderer): Int {
        return 0;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return 1;
    }

    fun fillGradientX(context: DrawContext, startX: Int, startY: Int, endX: Int, endY: Int, colorStart: Int, colorEnd: Int) {
        val matrix4f = context.matrices.peek().positionMatrix;
        val vertexConsumer = context.getProperty<VertexConsumerProvider.Immediate>("vertexConsumers").getBuffer(RenderLayer.getGui());
        vertexConsumer.vertex(matrix4f, startX.toFloat(), startY.toFloat(), 0f).color(colorStart);
        vertexConsumer.vertex(matrix4f, startX.toFloat(), endY.toFloat(), 0f).color(colorStart);
        vertexConsumer.vertex(matrix4f, endX.toFloat(), endY.toFloat(), 0f).color(colorEnd);
        vertexConsumer.vertex(matrix4f, endX.toFloat(), startY.toFloat(), 0f).color(colorEnd);
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int
    ) {
        val (front, back) = x0.toRangeSize(innerWidth).splitToTwo();

        context.matrices.push();
        fillGradientX(context, front.first, y0, front.last + 1, y0 + 1, color.toInt(0), color.toInt(200));
        fillGradientX(context, back.first, y0, back.last + 1, y0 + 1, color.toInt(200), color.toInt(0));
        context.matrices.pop();
    }
}