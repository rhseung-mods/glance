package com.rhseung.glance.tooltip.component

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class CenteredTextComponent(val text: Text) : GlanceTooltipComponent {
    override fun getHeight(textRenderer: TextRenderer): Int {
        return textRenderer.fontHeight - 1;     // 'g' 같은 거 때문에 9가 되는건데 8이 더 예쁘더라
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return textRenderer.getWidth(text);
    }

    override fun draw(context: DrawContext, textRenderer: TextRenderer, innerWidth: Int, innerHeight: Int, x0: Int, y0: Int) {
        context.drawText(textRenderer, text, x0, y0, -1, true);
    }
}