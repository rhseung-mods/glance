package com.rhseung.glance.util

import com.rhseung.glance.icon.AttributeIcon.Companion.toIcon
import com.rhseung.glance.icon.Icon
import com.rhseung.glance.icon.SignIcon
import com.rhseung.glance.icon.SignIcon.Companion.toSignIcon
import com.rhseung.glance.util.Draw.draw
import com.rhseung.glance.util.Color.Companion.toColor
import com.rhseung.glance.util.Util.toStringPretty
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.registry.entry.RegistryEntry
import kotlin.math.abs

class IconText(
    val attribute: RegistryEntry<EntityAttribute>,
    val value: Double,
    val isFixed: Boolean,
    val isMultiplier: Boolean
) {
    val icon = attribute.toIcon();
    val signIcon = Util.ifElse(isFixed, null, attribute.value().toSignIcon(value));
    val color = Util.ifElse(isFixed, Color.WHITE, attribute.value().getFormatting(value > 0).toColor());
    val text = abs(value).toStringPretty() + Util.ifElse(isMultiplier, SpecialChar.MULTIPLY, "");

    fun getHeight() = Icon.HEIGHT;
    fun getWidth(textRenderer: TextRenderer) = Icon.WIDTH +
            Draw.SPACE + textRenderer.getWidth(text) + Util.ifElse(signIcon != null, SignIcon.WIDTH + Draw.BETWEEN_SIGN_VALUE, 0);

    fun draw(context: DrawContext, textRenderer: TextRenderer, x0: Int, y0: Int, index: Int = 0): Int {
        var x = x0;
        x = icon.draw(context, x, y0, index) + Draw.SPACE;
        if (signIcon != null)
            x = signIcon.draw(context, x, y0) + Draw.BETWEEN_SIGN_VALUE;
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