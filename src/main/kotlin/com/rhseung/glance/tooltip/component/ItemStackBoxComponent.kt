package com.rhseung.glance.tooltip.component

import com.rhseung.glance.tooltip.TooltipDecor
import com.rhseung.glance.util.Color
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack

class ItemStackBoxComponent(
    val itemStack: ItemStack,
    val width: Int,
    val height: Int,
    val theme: TooltipDecor.Theme
) : GlanceTooltipComponent {

    override fun getWidth(textRenderer: TextRenderer): Int {
        return width + 2;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return height + 2;
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int
    ) {
        val innerx = x0 + 1;
        val innery = y0 + 1;

        context.matrices.push();

        // bg
        context.fill(innerx, innery, innerx + width, innery + height, 400, theme.bgColor1.toInt(200));
        // top
        context.fill(innerx, innery - 1, innerx + width, innery, 400, theme.outlineColor1.toInt(150));
        // bottom
        context.fill(innerx, innery + height, innerx + width, innery + height + 1, 400, theme.outlineColor1.toInt(150));
        // left
        context.fill(innerx - 1, innery, innerx, innery + height, 400, theme.outlineColor1.toInt(150));
        // right
        context.fill(innerx + width, innery, innerx + width + 1, innery + height, 400, theme.outlineColor1.toInt(150));
        // item
        context.matrices.translate(0f, 0f, 400f);
        context.drawItem(itemStack, innerx, innery);

        context.matrices.pop();
    }
}