package com.rhseung.glance.icon

import com.rhseung.glance.util.Color
import com.rhseung.glance.tooltip.Draw
import com.rhseung.glance.tooltip.Draw.draw
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

class IconText(val icon: Icon, val text: String, val color: Color = Color.WHITE) {
    fun getHeight() = Icon.HEIGHT;
    fun getWidth(textRenderer: TextRenderer) = Icon.WIDTH + Draw.SPACE + textRenderer.getWidth(text);

    fun draw(context: DrawContext, textRenderer: TextRenderer, x0: Int, y0: Int, index: Int = 0): Int {
        var x = x0;
        x = icon.draw(context, x, y0, index) + Draw.SPACE;
        x = text.draw(context, textRenderer, x, y0, color) + Draw.NEXT_ICON_MARGIN;
        return x;
    }

    companion object {
        fun List<IconText>.getWidth(textRenderer: TextRenderer): Int {
            if (isEmpty()) return 0;
            return sumOf { it.getWidth(textRenderer) } + Draw.NEXT_ICON_MARGIN * (size - 1);
        }

        fun List<IconText>.draw(context: DrawContext, textRenderer: TextRenderer, x0: Int, y0: Int, indices: List<Int> = List(this.size) { 0 }): Int {
            var x = x0;
            forEachIndexed { i, it ->
                x = it.draw(context, textRenderer, x, y0, indices[i]);
            }
            return x;
        }
    }
}