package com.rhseung.glance.tooltip.template

import com.rhseung.glance.tooltip.base.ItemStackTooltipComponent
import com.rhseung.glance.tooltip.base.SeparatorTooltipComponent
import com.rhseung.glance.tooltip.util.TooltipConstants
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.render.VertexConsumerProvider.Immediate
import net.minecraft.item.ItemStack
import kotlin.math.max

class DetailedTooltipTemplate(
    override val context: DrawContext,
    override val renderer: TextRenderer,
    override val titleComponents: List<OrderedTextTooltipComponent>,
    override val components: List<TooltipComponent>,
    val stack: ItemStack,
) : AbstractTooltipTemplate(context, renderer, titleComponents, components) {

    val pad = TooltipConstants.ITEM_PADDNIG;
    val lineMargin = TooltipConstants.LINE_MARGIN;
    val lastPad = pad - 1;

    val modelComponent =
        ItemStackTooltipComponent(stack, Color(stack.formattedName.style.color?.rgb ?: Color.WHITE.toInt()));
    val modelWidth = modelComponent.getWidth(renderer);
    val modelHeight = modelComponent.getHeight(renderer);

    override val width: Int = max(components.maxOfOrNull { pad + it.getWidth(renderer) + pad } ?: 0,
        pad + modelWidth + pad + titleComponentWidth + pad);

    override val height: Int = components.sumOf { it.getHeight(renderer) } + (getExactSize(
        components,
        renderer
    ) - 1).coerceAtLeast(0) * lineMargin + max(pad + modelHeight + pad, titleComponentHeight) +
            (if (components.isNotEmpty()) - pad + separatorComponentHeight + pad else 0) +
        - 1;   // last pad. font height는 9지만 8이 더 예뻐서 -1

    override val separatorComponent =
        SeparatorTooltipComponent(width, Color(stack.formattedName.style.color?.rgb ?: Color.WHITE.toInt()));
    override val separatorComponentWidth = width;

    override fun innerDraw(x0: Int, y0: Int) {
        var x = x0;
        var y = y0;

        if (titleComponents.size > 1)
            throw IllegalArgumentException("Title component size must be 1");

        val titleComponent = titleComponents[0];

        context.matrices.push();
        context.matrices.translate(0f, 0f, 400f);

        modelComponent.drawItems(
            renderer,
            x + pad,
            y + pad,
            width,
            height,
            context
        );

        val titleSpace = width - modelWidth - pad;

        titleComponent.drawText(
            renderer,
            x + pad + modelWidth + (titleSpace - titleComponentWidth) / 2,
            y + (pad + modelHeight + pad) / 2 - titleComponentHeight / 2,
            context.matrices.peek().positionMatrix,
            Util.get<Immediate>(context, "vertexConsumers")
        );

        y += max(pad + modelHeight, titleComponentHeight);

        if (components.isNotEmpty()) {
            separatorComponent.drawItems(
                renderer,
                x, y,
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
                x + TooltipConstants.ITEM_PADDNIG, y,
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
                x + TooltipConstants.ITEM_PADDNIG, y,
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