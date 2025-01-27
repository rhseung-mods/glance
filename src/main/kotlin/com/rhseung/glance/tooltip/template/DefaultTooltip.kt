package com.rhseung.glance.tooltip.template

import com.rhseung.glance.tooltip.TooltipDecor
import com.rhseung.glance.tooltip.component.CenteredLineComponent
import com.rhseung.glance.tooltip.component.GlanceTooltipComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.SeparatorComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.tooltip.component.XPaddingComponent
import com.rhseung.glance.tooltip.component.YPaddingComponent
import com.rhseung.glance.util.Util.joinTo
import net.minecraft.client.gui.tooltip.TooltipComponent

class DefaultTooltip(
    titles: List<TextComponent>,
    components: List<TooltipComponent>,
    floatingComponents: List<GlanceTooltipComponent>,
    theme: TooltipDecor.Theme
): GlanceTooltip(titles, components, floatingComponents, theme) {

    override var tooltip: MutableList<TooltipComponent>;

    init {
        tooltip = mutableListOf(
            YPaddingComponent(2),
            *this.titles.map { it.withTheme(theme) }.joinTo(YPaddingComponent(2)).toTypedArray(),
            YPaddingComponent(2),
        );

        if (components.isNotEmpty()) {
            tooltip.addAll(listOf(
                SeparatorComponent(theme.topOfOutline),
                YPaddingComponent(2),
                *this.components.joinTo(YPaddingComponent(2)).toTypedArray(),
                YPaddingComponent(2)
            ));
        }

        tooltip = tooltip.map {
            if (it is CenteredLineComponent)
                CenteredLineComponent(XPaddingComponent(2), *it.components.toTypedArray(), XPaddingComponent(2))
            else if (it is LineComponent)
                LineComponent(XPaddingComponent(2), *it.components.toTypedArray(), XPaddingComponent(2))
            else if (it is SeparatorComponent)
                it
            else
                LineComponent(XPaddingComponent(2), it, XPaddingComponent(2))
        }.toMutableList();
    }
}