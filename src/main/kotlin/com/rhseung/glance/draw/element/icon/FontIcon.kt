package com.rhseung.glance.draw.element.icon

import com.rhseung.glance.ModMain
import com.rhseung.glance.draw.element.Icon
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

class FontIcon(val file: String, val chars: String, index: Int = -1, width: Int = 5, height: Int = 7)
    : Icon(chars.length, width, height, index) {

    override val id: Identifier = ModMain.id("font/$file");

    override fun get(index: Int): Icon {
        throw Exception("not implemented");
    }

    fun get(char: Char): FontIcon {
        if (char !in chars)
            throw Exception("char($char) is not in chars($chars)");

        return FontIcon(file, chars, chars.indexOf(char), width, height);
    }

    override fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int {
        if (index < 0 || index >= variants)
            throw Error("index=$index is not valid");

        context.drawGuiTexture(RenderLayer::getGuiTextured, id, width * variants, height, width * index, 0, x0, y0, width, height);
        return x0 + width;
    }

    fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int, text: String): Int {
        if (text.isEmpty())
            return x0;

        var x = x0;
        for (char in text) {
            x = get(char).draw(context, renderer, x, y0) - 1;
        }
        return x + 1;
    }

    fun getWidth(text: String): Int {
        return if (text.isEmpty()) 0 else text.length * (width - 1) + 1;
    }

    companion object {
        val TINY_NUMBERS = FontIcon("tiny_numbers", "0123456789x");
    }
}