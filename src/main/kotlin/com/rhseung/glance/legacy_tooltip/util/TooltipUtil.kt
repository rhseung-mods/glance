package com.rhseung.glance.legacy_tooltip.util

import com.mojang.blaze3d.systems.RenderSystem
import com.rhseung.glance.draw.DrawHelper
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.legacy_tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.legacy_tooltip.template.DefaultTooltipTemplate
import com.rhseung.glance.legacy_tooltip.template.DetailedTooltipTemplate
import com.rhseung.glance.legacy_tooltip.util.TooltipSeparator.*
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.modify
import com.rhseung.glance.util.Util.toRangeSize
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.gui.tooltip.TooltipPositioner
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipData
import net.minecraft.text.Text
import net.minecraft.util.Rarity

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
    fun ItemStack.getTooltipDataClient(client: MinecraftClient): List<TooltipData> {
        val item = this.item;
        val original = item.getTooltipData(this);

        val tooltipDatas: MutableList<TooltipData> = TooltipDataFactoryManager.find(item, this, client).toMutableList();
        original.ifPresent { data -> tooltipDatas.add(0, data) };

//        compound.components = compound.components.filterNot { component ->
//            component.getHeight(client.textRenderer) == 0 &&
//            ((component as? AbstractTooltip<*>)?.canVanish ?: true)
//        }.toMutableList();

        return tooltipDatas;
    }

    @JvmName("drawTooltipText")
    fun drawTooltip(
        context: DrawContext,
        textRenderer: TextRenderer,
        texts: List<Text>,
        datas: List<TooltipData>,
        mouseX: Int,
        mouseY: Int,
        positioner: TooltipPositioner = HoveredTooltipPositioner.INSTANCE,
        stack: ItemStack? = null,
    ) {
        val components: MutableList<TooltipComponent> = texts.map(::TextComponent).toMutableList();
        val tooltipComponents = datas.map(TooltipComponent::of);
        components.addAll(if (tooltipComponents.isEmpty()) 0 else 1, tooltipComponents);

        drawTooltip(context, textRenderer, tooltipComponents, mouseX, mouseY, positioner, stack);
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
        if (tooltipComponents.any { it is OrderedTextTooltipComponent })
            error("OrderedTextTooltipComponent 사용 금지");

        val compoundIdx = if (stack != null) 1 else tooltipComponents.indexOfFirst { it !is TextComponent };
        val titleComponents: List<TextComponent> = (if (compoundIdx == -1) tooltipComponents else tooltipComponents.slice(0..<compoundIdx)) as List<TextComponent>;
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

        val color = Color(stack?.formattedName?.style?.color?.rgb ?: Color.WHITE.rgb());

        // Render tooltip background
        val background_x0 = x;
        val background_y0 = y;
        val backgroundX = background_x0.toRangeSize(tooltip.width + TooltipConstants.TOOLTIP_FRAME * 2);
        val backgroundY = background_y0.toRangeSize(tooltip.height + TooltipConstants.TOOLTIP_FRAME * 2);

        val innerBackgroundX = backgroundX.modify(TooltipConstants.TOOLTIP_FRAME, -TooltipConstants.TOOLTIP_FRAME);
        val innerBackgroundY = backgroundY.modify(TooltipConstants.TOOLTIP_FRAME, -TooltipConstants.TOOLTIP_FRAME);

        context.matrices.push();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
//        DrawHelper.drawSprite(context, DrawHelper.BACKGROUND, backgroundX, backgroundY, z, color);
        context.fillGradient(
            innerBackgroundX.first, innerBackgroundY.first,
            innerBackgroundX.last + 1, innerBackgroundY.last + 1,
            z, color.darker(0.85f).toInt(240), Color.BLACK.toInt(240)
        );

        // top
        context.fill(
            backgroundX.first + 1, backgroundY.first, backgroundX.last + 1 - 1, innerBackgroundY.first,
            z, color.darker(0.85f).toInt(240)
        );

        // bottom
        context.fill(
            backgroundX.first + 1, innerBackgroundY.last + 1, backgroundX.last + 1 - 1, backgroundY.last + 1,
            z, Color.BLACK.toInt(240)
        );

        // left
        context.fillGradient(
            backgroundX.first, backgroundY.first + 1, innerBackgroundX.first, backgroundY.last,
            z, color.darker(0.85f).toInt(240), Color.BLACK.toInt(240)
        );

        // right
        context.fillGradient(
            innerBackgroundX.last + 1, backgroundY.first + 1, backgroundX.last + 1, backgroundY.last,
            z, color.darker(0.85f).toInt(240), Color.BLACK.toInt(240)
        );

        RenderSystem.disableBlend();
        context.matrices.pop();

        context.matrices.push();
        context.matrices.translate(0f, 0f, z.toFloat());
        DrawHelper.drawBorder(context, backgroundX.modify(1, -1), backgroundY.modify(1, -1), z) { color.darker(it * 0.7f) };
        context.matrices.pop();

        tooltip.innerDraw(x + TooltipConstants.TOOLTIP_FRAME, y + TooltipConstants.TOOLTIP_FRAME);
    }
}