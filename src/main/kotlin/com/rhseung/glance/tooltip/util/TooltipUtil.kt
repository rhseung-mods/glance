package com.rhseung.glance.tooltip.util

import com.rhseung.glance.draw.DrawHelper
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.base.CompoundTooltip
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.tooltip.template.DefaultTooltipTemplate
import com.rhseung.glance.tooltip.template.DetailedTooltipTemplate
import com.rhseung.glance.tooltip.util.TooltipSeparator.*
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.getProperty
import com.rhseung.glance.util.Util.modify
import com.rhseung.glance.util.Util.toRangeSize
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.gui.tooltip.TooltipPositioner
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipData
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Rarity
import java.util.*

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

    @JvmName("drawTooltipText")
    fun drawTooltip(
        context: DrawContext,
        textRenderer: TextRenderer,
        text: List<Text>,
        data: Optional<TooltipData>,
        mouseX: Int,
        mouseY: Int,
        positioner: TooltipPositioner = HoveredTooltipPositioner.INSTANCE,
        stack: ItemStack? = null,
    ) {
        val list = text.map(Text::asOrderedText).map(TooltipComponent::of).toMutableList();
        data.ifPresent { datax: TooltipData -> list.add(if (list.isEmpty()) 0 else 1, TooltipComponent.of(datax)) };
        drawTooltip(context, textRenderer, list, mouseX, mouseY, positioner, stack);
    }

    @JvmName("drawTooltipOrderedText")
    fun drawTooltip(
        context: DrawContext,
        textRenderer: TextRenderer,
        text: List<OrderedText>,
        data: Optional<TooltipData>,
        mouseX: Int,
        mouseY: Int,
        positioner: TooltipPositioner = HoveredTooltipPositioner.INSTANCE,
        stack: ItemStack? = null,
    ) {
        val list = text.map(TooltipComponent::of).toMutableList();
        data.ifPresent { datax: TooltipData -> list.add(if (list.isEmpty()) 0 else 1, TooltipComponent.of(datax)) };
        drawTooltip(context, textRenderer, list, mouseX, mouseY, positioner, stack);
    }

    fun drawTooltip(
        context: DrawContext,
        textRenderer: TextRenderer,
        tooltipComponents: List<TooltipComponent>,
        mouseX: Int,
        mouseY: Int,
        positioner: TooltipPositioner,
        stack: ItemStack? = null
    ) {
        if (tooltipComponents.isEmpty())
            return;

        // TODO: 첫 component가 OrderedTextTooltipComponent가 아닐 경우, 0번째 인덱스에 CompoundTooltipComponent가 들어오는 경우를 처리

        val compoundIdx = if (stack != null) 1 else tooltipComponents.indexOfFirst { it is CompoundTooltip };
        val titleComponents: List<OrderedTextTooltipComponent> = (if (compoundIdx == -1) tooltipComponents else tooltipComponents.slice(0..<compoundIdx)) as List<OrderedTextTooltipComponent>;
        val components: List<TooltipComponent> = if (compoundIdx == -1) listOf() else tooltipComponents.slice(compoundIdx..<tooltipComponents.size);

        if (titleComponents.isEmpty())
            return;

        val tooltip = if (Screen.hasShiftDown() && stack != null && titleComponents.size == 1)
            DetailedTooltipTemplate(context, textRenderer, titleComponents, components, stack)
        else
            DefaultTooltipTemplate(context, textRenderer, titleComponents, components, stack);

        // tooltip background position
        val vector2ic = positioner.getPosition(context.scaledWindowWidth, context.scaledWindowHeight, mouseX, mouseY, tooltip.width + TooltipConstants.TOOLTIP_FRAME * 2, tooltip.height  + TooltipConstants.TOOLTIP_FRAME * 2);
        val x = vector2ic.x();
        val y = vector2ic.y();
        val z = 400;

        val color = Color(stack?.formattedName?.style?.color?.rgb ?: Color.WHITE.toInt());

        // Render tooltip background
        val background_x0 = x;
        val background_y0 = y;
        val backgroundX = background_x0.toRangeSize(tooltip.width + TooltipConstants.TOOLTIP_FRAME * 2);
        val backgroundY = background_y0.toRangeSize(tooltip.height + TooltipConstants.TOOLTIP_FRAME * 2);

        context.matrices.push();
        DrawHelper.drawSprite(context, DrawHelper.BACKGROUND, backgroundX, backgroundY, z, color);
        DrawHelper.drawBorder(context, backgroundX.modify(1, -1), backgroundY.modify(1, -1), z) { color.darker(it * 0.7f) };
        context.matrices.pop();

        tooltip.innerDraw(x + TooltipConstants.TOOLTIP_FRAME, y + TooltipConstants.TOOLTIP_FRAME);
    }
}