package com.rhseung.glance.tooltip.util

import com.rhseung.glance.draw.DrawHelper
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.base.ModelTooltip
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.tooltip.util.TooltipSeparator.*
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.gui.tooltip.TooltipPositioner
import net.minecraft.client.render.VertexConsumerProvider.Immediate
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipData
import net.minecraft.text.Text
import net.minecraft.util.Rarity
import java.util.*
import javax.tools.Tool
import kotlin.math.max

object TooltipUtil {
    fun Rarity.toTooltipSeparator(): TooltipSeparator {
        return when (this) {
            Rarity.COMMON -> COMMON;
            Rarity.UNCOMMON -> UNCOMMON;
            Rarity.RARE -> RARE;
            Rarity.EPIC -> EPIC;
            else -> throw IllegalArgumentException("Invalid rarity: $this");
        }
    }

    /**
     * [net.minecraft.item.Item.getTooltipData]
     */
    fun ItemStack.getTooltipDataWithClient(client: MinecraftClient): Optional<TooltipData> {
        val item = this.item;
        val original = item.getTooltipData(this);

        val compound = TooltipDataFactoryManager.find(item, this, client);
        compound.components = compound.components.filterNot { component ->
            component.getHeight(client.textRenderer) == 0 &&
            ((component as? AbstractTooltip<*>)?.canVanish ?: true)
        }.toMutableList();

        original.ifPresent { data -> compound.add(0, data) };

        return if (compound.size() > 0) Optional.of(compound) else original;
    }

    fun drawTooltip(
        context: DrawContext,
        textRenderer: TextRenderer,
        text: List<Text>,
        data: Optional<TooltipData>,
        x: Int,
        y: Int,
        stack: ItemStack
    ) {
        val list = text.map(Text::asOrderedText).map(TooltipComponent::of).toMutableList();
        data.ifPresent { datax: TooltipData -> list.add(if (list.isEmpty()) 0 else 1, TooltipComponent.of(datax)) };
        drawTooltip(context, textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE, stack);
    }

    private fun usePadding(components: List<TooltipComponent>): Boolean {
        var isText = 0
        var isNotText = 0

        for (tooltip in components) {
            if (tooltip is OrderedTextTooltipComponent) isText++
            else isNotText++
        }

        return isText >= 1 && isNotText >= 1;
    }

    fun drawTooltip(
        context: DrawContext,
        textRenderer: TextRenderer,
        components: List<TooltipComponent>,
        x0: Int,
        y0: Int,
        positioner: TooltipPositioner,
        stack: ItemStack
    ) {
        if (components.isEmpty())
            return;

        // TODO: 첫 component가 OrderedTextTooltipComponent가 아닐 경우, 0번째 인덱스에 CompoundTooltipComponent가 들어오는 경우를 처리

        val titleComponent: OrderedTextTooltipComponent = components[0] as OrderedTextTooltipComponent;
        val modelComponent: ModelTooltip = TooltipComponent.of(ModelTooltip.ModelTooltipData(stack.item, stack, MinecraftClient.getInstance())) as ModelTooltip;
        val components = components.drop(1);

        val tooltipWidth = max(
            components.maxOfOrNull { it.getWidth(textRenderer) } ?: 0,
            titleComponent.getWidth(textRenderer) + modelComponent.getWidth(textRenderer)
        );
        var tooltipHeight = components.sumOf { it.getHeight(textRenderer) } +
            modelComponent.getHeight(textRenderer);    // = ITEM_PADDNIG + ITEM_SLOT_SIZE + ITEM_PADDNIG

        if (components.isNotEmpty())
            tooltipHeight += 1 +     // TooltipSeparator
            TooltipConstants.ITEM_PADDNIG;

        if (usePadding(components))
            tooltipHeight += TooltipConstants.BETWEEN_TOOLTIP_TEXT;

        val vector2ic = positioner.getPosition(context.scaledWindowWidth, context.scaledWindowHeight, x0, y0, tooltipWidth, tooltipHeight);
        val x0Hover = vector2ic.x();
        val y0Hover = vector2ic.y();
        val z = 400;

        context.matrices.push();
        context.matrices.translate(0f, 0f, z.toFloat());
        context.matrices.pop();

        val rarity = stack.rarity;
        val color = Color(stack.formattedName.style.color?.rgb ?: Color.WHITE.toInt());

        // Render tooltip background
        TooltipBackground.render(context, x0Hover, y0Hover, tooltipWidth, tooltipHeight, z, color);

        // Render model
        TooltipBackground.render(context,
            x0Hover + TooltipConstants.ITEM_PADDNIG + 1, y0Hover + TooltipConstants.ITEM_PADDNIG + 1,
            TooltipConstants.ITEM_SLOT_SIZE - 4, TooltipConstants.ITEM_SLOT_SIZE - 4,
            z, color
        );

        context.matrices.push();
        context.matrices.translate(0f, 0f, z.toFloat());
        context.drawItem(stack,
            x0Hover + TooltipConstants.ITEM_PADDNIG + (TooltipConstants.ITEM_SLOT_SIZE - 16) / 2 - 1,
            y0Hover + TooltipConstants.ITEM_PADDNIG + (TooltipConstants.ITEM_SLOT_SIZE - 16) / 2 - 1
        );

        // Render title
        titleComponent.drawText(textRenderer,
            x0Hover + modelComponent.getWidth(textRenderer),
            y0Hover + TooltipConstants.ITEM_PADDNIG + TooltipConstants.ITEM_SLOT_SIZE / 2 - (textRenderer.fontHeight / 2 + 1),
            context.matrices.peek().positionMatrix,
            Util.get(context, "vertexConsumers") as Immediate
        );

        // Render separator
        if (components.isNotEmpty()) {
            DrawHelper.drawHorizontalLine(context, x0Hover, x0Hover + tooltipWidth / 2, y0Hover + modelComponent.getHeight(textRenderer), z) { color.darker(1 - it) };
            DrawHelper.drawHorizontalLine(context, x0Hover + tooltipWidth / 2 + 1, x0Hover + tooltipWidth, y0Hover + modelComponent.getHeight(textRenderer), z) { color.darker(it) };

//            rarity.toTooltipSeparator().draw(
//                context,
//                x0Hover,
//                x0Hover + tooltipWidth,
//                y0Hover + modelComponent.getHeight(textRenderer)
//            );
        }

        // Render components
        var y = y0Hover + modelComponent.getHeight(textRenderer) + 1 + TooltipConstants.ITEM_PADDNIG;
        if (usePadding(components))
            y += TooltipConstants.BETWEEN_TOOLTIP_TEXT;

        for (component in components) {
            component.drawText(textRenderer,
                x0Hover,
                y,
                context.matrices.peek().positionMatrix,
                Util.get(context, "vertexConsumers") as Immediate
            );

            y += component.getHeight(textRenderer);
        }

        y = y0Hover + modelComponent.getHeight(textRenderer) + 1 + TooltipConstants.ITEM_PADDNIG;

        for (component in components) {
            component.drawItems(textRenderer,
                x0Hover,
                y,
                tooltipWidth,
                tooltipHeight,
                context
            );

            y += component.getHeight(textRenderer);
        }

        context.matrices.pop();

//        val vector2ic = positioner.getPosition(context.scaledWindowWidth, context.scaledWindowHeight, x0, y0, tooltipWidth, tooltipHeight);
//        val x0Hover = vector2ic.x();
//        val y0Hover = vector2ic.y();
//
//        context.matrices.push();
//        val z = 400;
//
//        TooltipBackground.render(
//            context,
//            x0Hover,
//            y0Hover,
//            tooltipWidth,
//            tooltipHeight
//                + if (components.size <= 1) 0 else TooltipConstants.SEPARATOR_MARGIN
//                + if (usePadding(components)) TooltipConstants.BETWEEN_TOOLTIP_TEXT else 0,
//            z,
//            rarity.formatting.toColor()
//        );
//
//        context.matrices.translate(0.0f, 0.0f, z.toFloat());
//        var y = y0Hover;
//
//        titleComponent.drawText(
//            textRenderer,
//            x0Hover,
//            y,
//            context.matrices.peek().positionMatrix,
//            Util.get(context, "vertexConsumers") as Immediate
//        );
//        y += titleComponent.getHeight(textRenderer);
//
//        rarity.toTooltipSeparator().draw(
//            context,
//            x0Hover,
//            x0Hover + tooltipWidth,
//            y + titleComponent.getHeight(textRenderer) + TooltipConstants.SEPARATOR_MARGIN / 2
//        );
//        y += TooltipConstants.SEPARATOR_MARGIN;
//
//        for (r in 1..<components.size) {
//            val component = components[r];
//
//            if (r == 1)
//                y += TooltipConstants.SEPARATOR_MARGIN + if (usePadding(components)) TooltipConstants.BETWEEN_TOOLTIP_TEXT else 0;
//
//            component.drawText(
//                textRenderer,
//                x0Hover,
//                y,
//                context.matrices.peek().positionMatrix,
//                Util.get(context, "vertexConsumers") as Immediate
//            )
//
//            y += component.getHeight(textRenderer);
//        }
//
//        y = y0Hover;
//
//        for (r in 1..<components.size) {
//            val component = components[r];
//
//            component.drawItems(
//                textRenderer,
//                x0Hover,
//                y,
//                tooltipWidth,
//                tooltipHeight + TooltipConstants.SEPARATOR_MARGIN,
//                context
//            )
//
//            y += component.getHeight(textRenderer) + (if (r == 0) 2 else 0);
//        }
//
//        context.matrices.pop();
    }
}