package com.rhseung.glance.tooltip.component

import com.rhseung.glance.tooltip.TooltipDecor
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class TextComponent(val text: Text, val shift: Int = 0, val theme: TooltipDecor.Theme? = null) : GlanceTooltipComponent {
    constructor(text: String) : this(Text.of(text));
    constructor(text: Text): this(text, 0);

    fun withTheme(theme: TooltipDecor.Theme): TextComponent {
        return TextComponent(text, shift, theme);
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return textRenderer.fontHeight - 1;     // 'g' 같은 거 때문에 9가 되는건데 8이 더 예쁘더라
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return textRenderer.getWidth(text);
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int,
        outerX: Int,
        outerY: Int
    ) {
        if (theme?.title != null) {
            val coloredText = text.copy().withColor(theme.title.toInt());
            context.drawText(textRenderer, coloredText, x0, y0 + shift, -1, true);
        }
        else
            context.drawText(textRenderer, text, x0, y0 + shift, -1, true);
    }
}