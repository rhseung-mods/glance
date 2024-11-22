package com.rhseung.glance.tooltip

import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.ifElse
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object Draw {
    const val SPACE = 3;
    const val LINE_MARGIN = 2;
    const val NEXT_ICON_MARGIN = 8;
    const val SLOT_MARGIN = 12;

    fun String.draw(
        context: DrawContext,
        renderer: TextRenderer,
        x: Int,
        y: Int,
        color: Color = Color.WHITE,
    ): Int {
        context.drawText(renderer, this, x, y, color.toInt(), true);
        return x + renderer.getWidth(this);
    }

    fun Text.draw(
        context: DrawContext,
        renderer: TextRenderer,
        x: Int,
        y: Int,
        color: Color = Color.WHITE,
    ): Int {
        context.drawText(renderer, this, x, y, color.toInt(), true);
        return x + renderer.getWidth(this);
    }

    fun drawTooltip(
        context: DrawContext,
        renderer: TextRenderer,
        components: List<TooltipComponent>,
        vertexConsumers: VertexConsumerProvider.Immediate,
        stack: ItemStack,
        x: Int,
        y: Int,
        backgroundId: Identifier?,
    ) {
        if (components.isEmpty()) return;

        val width = components.maxOf { it.getWidth(renderer) };
        val height = ifElse(components.size == 1, -2, 0) + components.sumOf { it.getHeight(renderer) };

        val vec = HoveredTooltipPositioner.INSTANCE.getPosition(context.scaledWindowWidth, context.scaledWindowHeight, x, y, width, height);

        val x0 = vec.x();
        val y0 = vec.y();
        val z = 400;

        context.matrices.push();

        TooltipBackgroundRenderer.render(context, x0, y0, width, height, z, backgroundId);
        context.matrices.translate(0.0, 0.0, z.toDouble());

        var v = y0;
        components.forEachIndexed { i, component ->
            component.drawText(renderer, x0, v, context.matrices.peek().positionMatrix, vertexConsumers);
            v += component.getHeight(renderer) + ifElse(i == 0, 2, 0);
        }

        v = y0;
        components.forEachIndexed { i, component ->
            component.drawItems(renderer, x0, v, width, height, context);
            v += component.getHeight(renderer) + ifElse(i == 0, 2, 0);
        }

        context.matrices.pop();
    }
}