package com.rhseung.glance.draw

import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Draw.draw
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.OrderedText
import net.minecraft.text.PlainTextContent.Literal
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextContent

class Text(
    val content: String,
    val color: Color? = null
) : Drawable, net.minecraft.text.Text {

    override fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int {
        context.drawText(renderer, content, x0, y0, color?.toInt() ?: -1, true);
        return x0 + getWidth(renderer);
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return textRenderer.getWidth(content);
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return textRenderer.fontHeight;
    }

    override fun getStyle(): Style {
        return if (color != null)
            Style.EMPTY.withColor(color.toInt());
        else
            Style.EMPTY;
    }

    override fun getContent(): TextContent {
        return Literal(content);
    }

    override fun getSiblings(): MutableList<Text> {
        return mutableListOf();
    }

    override fun asOrderedText(): OrderedText {
        return OrderedText.EMPTY;
    }

    companion object {
        infix fun String.with(color: Color): com.rhseung.glance.draw.Text {
            return Text(this, color);
        }
    }
}