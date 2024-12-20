package com.rhseung.glance.tooltip.base

import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.template.AbstractTooltipTemplate.Companion.getExactSize
import com.rhseung.glance.tooltip.util.TooltipConstants
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.item.tooltip.TooltipData

class CompoundTooltip(private val data: CompoundTooltipData) : TooltipComponent {
    class CompoundTooltipData : TooltipData {
        var components = mutableListOf<TooltipComponent>();

        fun size() = components.size;

        fun add(data: TooltipData) {
            components.add(TooltipComponentFactoryManager.of(data));
        }

        fun add(index: Int, data: TooltipData) {
            components.add(index, TooltipComponentFactoryManager.of(data));
        }
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return data.components.sumOf { it.getHeight(textRenderer) } + (getExactSize(data.components, textRenderer) - 1).coerceAtLeast(0) * TooltipConstants.LINE_MARGIN;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return data.components.maxOf { it.getWidth(textRenderer) };
    }

    override fun drawItems(
        textRenderer: TextRenderer,
        x0: Int,
        y0: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        var y = y0;

        data.components.forEach {
            it.drawItems(textRenderer, x0, y, width, height, context);

            if (it.getHeight(textRenderer) == 0) return@forEach;
            y += it.getHeight(textRenderer) + TooltipConstants.LINE_MARGIN;
        }
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<CompoundTooltipData>(::CompoundTooltip);
        }
    }
}