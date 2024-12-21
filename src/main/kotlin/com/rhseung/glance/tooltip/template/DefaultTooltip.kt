package com.rhseung.glance.tooltip.template

import com.rhseung.glance.tooltip.TooltipDecor
import com.rhseung.glance.tooltip.component.CenteredLineComponent
import com.rhseung.glance.tooltip.component.GlanceTooltipComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.SeparatorComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.tooltip.component.XPaddingComponent
import com.rhseung.glance.tooltip.component.YPaddingComponent
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.joinTo
import com.rhseung.glance.util.Util.safeGet
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent

class DefaultTooltip(
    titles: List<TextComponent>,
    components: List<TooltipComponent>,
    theme: TooltipDecor.Theme
): GlanceTooltip(titles, components, theme) {

    override var tooltip: MutableList<TooltipComponent>;

    init {
        tooltip = mutableListOf(
            YPaddingComponent(2),
            *this.titles.joinTo(YPaddingComponent(2)).toTypedArray(),
            YPaddingComponent(2),
        );

        if (components.isNotEmpty()) {
            val textColor = titles[0].text.style.color ?: titles[0].text.siblings.safeGet(0)?.style?.color;
            val color = Color(textColor?.rgb ?: -1);

            tooltip.addAll(listOf(
                SeparatorComponent(theme.outlineColor1),
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