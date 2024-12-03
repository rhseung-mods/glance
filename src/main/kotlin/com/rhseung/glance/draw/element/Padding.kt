package com.rhseung.glance.draw.element

import com.rhseung.glance.draw.Drawable
import com.rhseung.glance.draw.DrawableLine
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import kotlin.io.path.Path

data class Padding(val size: Int) : Drawable {
    companion object {
        // TODO: 이건 y축 패딩이라 조금 다름. 수정 필요.
        val LINE_MARGIN = Padding(2);

        val SPACE = Padding(3);
        val BETWEEN_SIGN_VALUE = Padding(2);
        val SLOT_MARGIN = Padding(12);
        val NEXT_ICON = Padding(8);
        val ICON_START = Padding(1);
        val ENCHANT_TEST_ITEM_MARGIN = Padding(SLOT_MARGIN.size - SPACE.size);
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return size;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return textRenderer.fontHeight;
    }

    override fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int {
        return x0 + getWidth(renderer);
    }

    operator fun times(ratio: Float): Padding {
        return Padding((size * ratio).toInt());
    }

    operator fun div(ratio: Float): Padding {
        return Padding((size / ratio).toInt());
    }

    operator fun plus(line: DrawableLine): DrawableLine {
        return DrawableLine(this, *line.drawables);
    }

    operator fun plus(string: String): DrawableLine {
        return DrawableLine(this, GlanceText(string));
    }
}