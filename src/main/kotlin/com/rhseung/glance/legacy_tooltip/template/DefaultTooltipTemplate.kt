package com.rhseung.glance.legacy_tooltip.template

import com.rhseung.glance.legacy_tooltip.base.SeparatorTooltipComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.legacy_tooltip.util.TooltipConstants
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.render.VertexConsumerProvider.Immediate
import net.minecraft.item.ItemStack
import kotlin.math.max

class DefaultTooltipTemplate(
    override val context: DrawContext,
    override val renderer: TextRenderer,
    override val titleComponents: List<TextComponent>,
    override val components: List<TooltipComponent>,
    val stack: ItemStack? = null,
) : AbstractTooltipTemplate(context, renderer, titleComponents, components) {

    val pad = 2;
    val lineMargin = TooltipConstants.LINE_MARGIN;

    override val width: Int =
        max(components.maxOfOrNull { pad + it.getWidth(renderer) + pad } ?: 0, pad + titleComponentWidth + pad);

    override val height: Int = components.sumOf { it.getHeight(renderer) } + (getExactSize(components, renderer) - 1).coerceAtLeast(0) * lineMargin +
        pad + titleComponentHeight + pad + if (components.isNotEmpty()) - pad + separatorComponentHeight + pad else 0

    override val separatorComponent =
        SeparatorTooltipComponent(width, Color(stack?.formattedName?.style?.color?.rgb ?: Color.WHITE.rgb()));
    override val separatorComponentWidth = width;

    override fun innerDraw(x0: Int, y0: Int) {
        var x = x0 + pad;
        var y = y0 + pad;

        context.matrices.push();
        context.matrices.translate(0f, 0f, 400f);

        // TODO: title이 여러 줄일 때 component가 여러 개인건가?

        for (titleComponent in titleComponents) {
            titleComponent.drawText(
                renderer,
                x,
                y,
                context.matrices.peek().positionMatrix,
                Util.get<Immediate>(context, "vertexConsumers")
            );

            y += titleComponent.getHeight(renderer) + lineMargin;
        }

        y -= lineMargin;

        if (components.isNotEmpty()) {
            separatorComponent.drawItems(
                renderer,
                x - pad, y,
                width,
                height,
                context
            );

            y += separatorComponentHeight;
        }

        val y_start = y;
        y = y_start;

        for (component in components) {
            component.drawText(
                renderer,
                x, y,
                context.matrices.peek().positionMatrix,
                Util.get<Immediate>(context, "vertexConsumers")
            );

            if (component.getHeight(renderer) == 0) continue;
            y += component.getHeight(renderer) + lineMargin;
        }

        y = y_start;

        for (component in components) {
            component.drawItems(
                renderer,
                x, y,
                width,
                height,
                context
            );

            if (component.getHeight(renderer) == 0) continue;
            y += component.getHeight(renderer) + lineMargin;
        }

        context.matrices.pop();
    }
}