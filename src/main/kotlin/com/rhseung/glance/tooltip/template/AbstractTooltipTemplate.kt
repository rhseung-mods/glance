package com.rhseung.glance.tooltip.template

import com.rhseung.glance.tooltip.base.SeparatorTooltipComponent
import com.rhseung.glance.tooltip.util.TooltipConstants
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent

abstract class AbstractTooltipTemplate(
    open val context: DrawContext,
    open val renderer: TextRenderer,
    open val titleComponents: List<OrderedTextTooltipComponent>,
    open val components: List<TooltipComponent>
) {
    abstract val width: Int;
    abstract val height: Int;

    val titleComponentWidth: Int
        get() = titleComponents.maxOf { it.getWidth(renderer) };
    val titleComponentHeight: Int
        get() = titleComponents.sumOf { it.getHeight(renderer) } + (titleComponents.size - 1).coerceAtLeast(0) * TooltipConstants.LINE_MARGIN;

    abstract val separatorComponent: SeparatorTooltipComponent;
    abstract val separatorComponentWidth: Int;
    val separatorComponentHeight: Int = TooltipConstants.ITEM_PADDNIG + 1 + TooltipConstants.ITEM_PADDNIG;

    abstract fun innerDraw(x0: Int, y0: Int);

    companion object {
//        fun usePadding(components: List<TooltipComponent>, renderer: TextRenderer): Boolean {
//            var isText = 0
//            var isNotText = 0
//
//            for (component in components) {
//                if (component.getHeight(renderer) == 0) continue;
//
//                if (component is OrderedTextTooltipComponent) isText++
//                else isNotText++
//            }
//
//            return isText >= 1 && isNotText >= 1;
//        }

        fun getExactSize(components: List<TooltipComponent>, textRenderer: TextRenderer): Int {
            return components.count { it.getHeight(textRenderer) > 0 };
        }
    }
}