package com.rhseung.glance.draw

import com.rhseung.glance.draw.Text.Companion.with
import com.rhseung.glance.icon.SignIcon
import com.rhseung.glance.icon.SignIcon.Companion.toSignIcon
import com.rhseung.glance.util.Color.Companion.toColor
import com.rhseung.glance.util.SpecialChar
import com.rhseung.glance.util.Util.toStringPretty
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.attribute.EntityAttribute
import kotlin.math.abs

class Number(
    val number: Double,
    val isFixed: Boolean = false,
    val isMultiplier: Boolean = false,
    val attribute: EntityAttribute,
) : Drawable {

    private val color = attribute.getFormatting(number > 0).toColor();
    private val icon = attribute.toSignIcon(number);
    private val text = ((if (!isFixed) abs(number) else number).toStringPretty() + (if (isMultiplier) SpecialChar.MULTIPLY else "")) with color;
    private val tooltip = if (!isFixed && icon != null) icon + text else DrawableLine(text);

    override fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int {
        return tooltip.draw(context, renderer, x0, y0).x;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return tooltip.getWidth(textRenderer);
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return tooltip.getHeight(textRenderer);
    }
}