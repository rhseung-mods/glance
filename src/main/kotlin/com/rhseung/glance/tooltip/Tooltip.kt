package com.rhseung.glance.tooltip

import com.rhseung.glance.legacy_tooltip.util.TooltipConstants
import com.rhseung.glance.tooltip.component.GlanceTooltipComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.tooltip.content.TooltipContentRegistry
import com.rhseung.glance.tooltip.template.DefaultTooltip
import com.rhseung.glance.tooltip.template.DetailTooltip
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util
import com.rhseung.glance.util.Util.getProperty
import com.rhseung.glance.util.Util.ifElse
import com.rhseung.glance.util.Util.safeGet
import com.rhseung.glance.util.Util.toRangeSize
import com.rhseung.glance.util.Util.toText
import net.minecraft.block.OxidizableBlock
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.gui.tooltip.TooltipPositioner
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Rarity

object Tooltip {
    fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        tooltipComponents: List<TooltipComponent>,
        positioner: TooltipPositioner,
        mouseX: Int,
        mouseY: Int,
        stack: ItemStack? = null,
    ) {
        val everyComponents = tooltipComponents.map {
            if (it is OrderedTextTooltipComponent)
                TextComponent(it.getProperty<OrderedText>("text").toText())
            else
                it
        }.toMutableList();

        val titleStyle = (everyComponents[0] as TextComponent).text.style;
        var titleEndIdx = everyComponents.indexOfFirst { it !is TextComponent || !it.text.style.equals(titleStyle) };
        if (titleEndIdx == -1)
            titleEndIdx = everyComponents.size;

        if (stack != null)
            TooltipContentRegistry.find(stack.item, stack).forEach { everyComponents.addAll(titleEndIdx, it.getComponents()) }

        val titleComponents: List<TextComponent> = everyComponents.subList(0, titleEndIdx) as? List<TextComponent> ?: error("Title components must be TextComponent");
        var components: List<TooltipComponent> = everyComponents.subList(titleEndIdx, everyComponents.size);

        if (titleComponents.isEmpty())
            return;

        components = components.dropWhile { it is TextComponent && it.text.string.isEmpty() };

        val firstTitle = titleComponents[0].text.asOrderedText().toText();
        val textColor = firstTitle.style.color ?: firstTitle.siblings.safeGet(0)?.style?.color;
        val color = if (textColor?.rgb != null) Color(textColor.rgb) else null;

        val theme: TooltipDecor.Theme =
        if (stack != null) {
            val id = Registries.ITEM.getId(stack.item);

            if (stack.contains(DataComponentTypes.STORED_ENCHANTMENTS))
                TooltipDecor.Themes.ENCHANT;
            else if (stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE))
                TooltipDecor.Themes.MUSIC;
            else if ("copper" in id.path)
                TooltipDecor.Themes.COPPER;
            else if ("gold" in id.path)
                TooltipDecor.Themes.GOLD;
            else if ("iron" in id.path)
                TooltipDecor.Themes.IRON;
            else if ("netherite" in id.path)
                TooltipDecor.Themes.NETHERITE;
            else if ("ender" in id.path)
                TooltipDecor.Themes.ENDER;
            else if ("sculk" in id.path || "echo" in id.path)
                TooltipDecor.Themes.ECHO;
            else when (stack.rarity) {
                Rarity.COMMON -> TooltipDecor.Themes.DEFAULT;
                Rarity.UNCOMMON -> TooltipDecor.Themes.UNCOMMON;
                Rarity.RARE -> TooltipDecor.Themes.RARE;
                Rarity.EPIC -> TooltipDecor.Themes.EPIC;
            }
        } else {
            if (color != null)
                TooltipDecor.Theme(color);
            else
                TooltipDecor.Themes.DEFAULT;
        }

        val tooltip = if (stack != null && Screen.hasShiftDown())
            DetailTooltip(titleComponents, components, theme, stack);
        else
            DefaultTooltip(titleComponents, components, theme);

        val width = tooltip.getWidth(textRenderer);
        val height = tooltip.getHeight(textRenderer);
        val tooltipWidth = width + 2 * TooltipConstants.TOOLTIP_FRAME;
        val tooltipHeight = height + 2 * TooltipConstants.TOOLTIP_FRAME;

        val vector2ic = positioner.getPosition(context.scaledWindowWidth, context.scaledWindowHeight, mouseX, mouseY, tooltipWidth, tooltipHeight);
        val x = vector2ic.x();
        val y = vector2ic.y();
        val z = 400;

        val innerX = (x + TooltipConstants.TOOLTIP_FRAME).toRangeSize(width);
        val innerY = (y + TooltipConstants.TOOLTIP_FRAME).toRangeSize(height);

        TooltipDecor.drawBackground(context, innerX, innerY, z, theme);
        TooltipDecor.drawBorder(context, innerX, innerY, z, theme);

        context.matrices.push();
        context.matrices.translate(0f, 0f, z.toFloat());
        tooltip.draw(context, textRenderer, innerX.first, innerY.first);
        context.matrices.pop();
    }
}