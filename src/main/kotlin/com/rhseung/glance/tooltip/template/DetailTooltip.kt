package com.rhseung.glance.tooltip.template

import com.rhseung.glance.tooltip.TooltipDecor
import com.rhseung.glance.tooltip.component.Armor3DBoxComponent
import com.rhseung.glance.tooltip.component.ItemStackBoxComponent
import com.rhseung.glance.tooltip.component.CenteredLineComponent
import com.rhseung.glance.tooltip.component.CenteredTextComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.SeparatorComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.tooltip.component.XPaddingComponent
import com.rhseung.glance.tooltip.component.YPaddingComponent
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.joinTo
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack

class DetailTooltip(
    titles: List<TextComponent>,
    components: List<TooltipComponent>,
    theme: TooltipDecor.Theme,
    stack: ItemStack,
) : GlanceTooltip(titles, components, theme) {
    override var tooltip: MutableList<TooltipComponent>;

    init {
        val textColor = titles[0].text.style.color ?: titles[0].text.siblings[0]?.style?.color;
        val color = Color(textColor?.rgb ?: -1);

        tooltip = mutableListOf(
            YPaddingComponent(3),
            CenteredLineComponent(
                if (stack.item !is ArmorItem) ItemStackBoxComponent(stack, 16, 16, theme) else Armor3DBoxComponent(stack, 16, 16, theme),
                XPaddingComponent(3),
                CenteredTextComponent(stack.formattedName)
            ),
            YPaddingComponent(3),
        )

        if (components.isNotEmpty()) {
            tooltip.addAll(listOf(
                SeparatorComponent(theme.outlineColor1),
                YPaddingComponent(3),
                *this.components.joinTo(YPaddingComponent(3)).toTypedArray(),
                YPaddingComponent(3)
            ));
        }

        tooltip = tooltip.map {
            if (it is CenteredLineComponent)
                CenteredLineComponent(XPaddingComponent(3), *it.components.toTypedArray(), XPaddingComponent(3))
            else if (it is LineComponent)
                LineComponent(XPaddingComponent(3), *it.components.toTypedArray(), XPaddingComponent(3))
            else if (it is SeparatorComponent)
                it
            else
                LineComponent(XPaddingComponent(3), it, XPaddingComponent(3))
        }.toMutableList();
    }
}