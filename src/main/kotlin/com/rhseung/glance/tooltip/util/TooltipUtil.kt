package com.rhseung.glance.tooltip.util

import com.rhseung.glance.draw.DrawHelper
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.tooltip.util.TooltipSeparator.*
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util
import com.rhseung.glance.util.Util.modify
import com.rhseung.glance.util.Util.size
import com.rhseung.glance.util.Util.splitToTwo
import com.rhseung.glance.util.Util.toRangeSize
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
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Rarity
import java.util.*
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
        val modelComponentSize =
                TooltipConstants.ITEM_PADDNIG +
                TooltipConstants.MODEL_FRAME +
                TooltipConstants.ITEM_SLOT_SIZE +
                TooltipConstants.MODEL_FRAME +
                TooltipConstants.ITEM_PADDNIG;
        val components = components.drop(1);

        val tooltipWidth = TooltipConstants.TOOLTIP_FRAME * 2 + max(
            components.maxOfOrNull { TooltipConstants.ITEM_PADDNIG + it.getWidth(textRenderer) + TooltipConstants.ITEM_PADDNIG } ?: 0,
            modelComponentSize + titleComponent.getWidth(textRenderer) + TooltipConstants.ITEM_PADDNIG
        );

        var tooltipHeight = TooltipConstants.TOOLTIP_FRAME + modelComponentSize + TooltipConstants.TOOLTIP_FRAME;

        if (components.isNotEmpty())
            tooltipHeight += 1 +     // TooltipSeparator
            TooltipConstants.ITEM_PADDNIG + components.sumOf { it.getHeight(textRenderer) } + TooltipConstants.ITEM_PADDNIG;

        if (usePadding(components))
            tooltipHeight += TooltipConstants.BETWEEN_TOOLTIP_TEXT;

        val vector2ic = positioner.getPosition(context.scaledWindowWidth, context.scaledWindowHeight, x0, y0, tooltipWidth, tooltipHeight);

        // tooltip background position
        val x = vector2ic.x();
        val y = vector2ic.y();
        val z = 400;

        val rarity = stack.rarity;
        val color = Color(stack.formattedName.style.color?.rgb ?: Color.WHITE.toInt());

        // Render tooltip background
        val background_x0 = x;
        val background_y0 = y;
        val backgroundX = background_x0.toRangeSize(tooltipWidth);
        val backgroundY = background_y0.toRangeSize(tooltipHeight);

        context.matrices.push();
        DrawHelper.drawSprite(context, DrawHelper.BACKGROUND, backgroundX, backgroundY, z, color);
        DrawHelper.drawBorder(context, backgroundX.modify(1, -1), backgroundY.modify(1, -1), z) { color.darker(it * 0.7f) };
        context.matrices.pop();

        // Render model background
        val innerBackground_x0 = background_x0 + TooltipConstants.TOOLTIP_FRAME;
        val innerBackground_y0 = background_y0 + TooltipConstants.TOOLTIP_FRAME;
        val innerBackgroundX = innerBackground_x0.toRangeSize(tooltipWidth - TooltipConstants.TOOLTIP_FRAME * 2);
        val innerBackgroundY = innerBackground_y0.toRangeSize(tooltipHeight - TooltipConstants.TOOLTIP_FRAME * 2);

        val modelBackground_x0 = innerBackground_x0 + TooltipConstants.ITEM_PADDNIG;
        val modelBackground_y0 = innerBackground_y0 + TooltipConstants.ITEM_PADDNIG;
        val modelBackgroundSize = TooltipConstants.MODEL_FRAME + TooltipConstants.ITEM_SLOT_SIZE + TooltipConstants.MODEL_FRAME;
        val modelBackgroundX = modelBackground_x0.toRangeSize(modelBackgroundSize);
        val modelBackgroundY = modelBackground_y0.toRangeSize(modelBackgroundSize);

        val innerModelBackground_x0 = modelBackground_x0 + TooltipConstants.MODEL_FRAME;
        val innerModelBackground_y0 = modelBackground_y0 + TooltipConstants.MODEL_FRAME;

        context.matrices.push();
        DrawHelper.drawSprite(context, DrawHelper.BACKGROUND, modelBackgroundX, modelBackgroundY, z, color);
        DrawHelper.drawBorder(context, modelBackgroundX, modelBackgroundY, z, color.darker(0.6f));
        context.matrices.pop();

        // Render model
        context.matrices.push();
        DrawHelper.drawItem(context, stack, innerModelBackground_x0, innerModelBackground_y0, z);
        context.matrices.pop();

        // Render title
        val titleSpaceSize = innerBackgroundX.size() - (modelComponentSize - TooltipConstants.ITEM_PADDNIG);
        val titleSpace_x0 = innerBackground_x0 + (modelComponentSize - TooltipConstants.ITEM_PADDNIG);
        val text: OrderedText = Util.get(titleComponent, "text") as OrderedText;

        context.matrices.push();
        context.matrices.translate(0f, 0f, z.toFloat());

        titleComponent.drawText(textRenderer,
            titleSpace_x0 + (titleSpaceSize - textRenderer.getWidth(text)) / 2,
            innerBackground_y0 + modelComponentSize / 2 - textRenderer.fontHeight / 2,
            context.matrices.peek().positionMatrix,
            Util.get(context, "vertexConsumers") as Immediate
        );

        // Render separator
        if (components.isNotEmpty()) {
            val splited = innerBackgroundX.splitToTwo();

            DrawHelper.drawHorizontalLine(context,
                splited.first,
                innerBackground_y0 + modelComponentSize
            , z) { color.darker(1 - it) };

            DrawHelper.drawHorizontalLine(context,
                splited.second,
                innerBackground_y0 + modelComponentSize
            , z) { color.darker(it) };
        }

        // Render components
        val component_y0 = innerBackground_y0 + modelComponentSize + 1 + TooltipConstants.ITEM_PADDNIG + 1; // 마지막 1은 아이콘이 글씨보다 1씩 높게 그려지기 때문에, 그것이 패딩이 3이 되도록 하기 위해 1을 더 더해야 함. 또한, 글자의 height가 9가 아니라 8이라서 1을 아래로 더해야함
        var component_y = component_y0;

        if (usePadding(components))
            component_y += TooltipConstants.BETWEEN_TOOLTIP_TEXT;

        for (component in components) {
            component.drawText(textRenderer,
                innerBackground_x0 + TooltipConstants.ITEM_PADDNIG, component_y,
                context.matrices.peek().positionMatrix,
                Util.get(context, "vertexConsumers") as Immediate
            );

            component_y += component.getHeight(textRenderer);
        }

        component_y = component_y0;

        for (component in components) {
            component.drawItems(textRenderer,
                innerBackground_x0 + TooltipConstants.ITEM_PADDNIG, component_y,
                tooltipWidth,
                tooltipHeight,
                context
            );

            component_y += component.getHeight(textRenderer) + TooltipConstants.LINE_MARGIN;
        }

        context.matrices.pop();
    }
}