package com.rhseung.glance.overlay

import com.rhseung.glance.util.Color
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class StackCountOverlay(item: Item, itemStack: ItemStack) : GlanceStackOverlay(item, itemStack) {
    override fun render(
        context: DrawContext,
        textRenderer: TextRenderer,
        x: Int,
        y: Int,
        stackCountText: String?
    ) {
        if (itemStack.count != 1 || stackCountText != null) {
            val stackCountText = stackCountText ?: itemStack.count.toString();
            val textWidth = textRenderer.getWidth(stackCountText);
            val i = x + 19 - 2 - textWidth;
            val j = y + 6 + 3;

            context.matrices.push();
            context.matrices.translate(0f, 0f, 200f);
            context.drawText(textRenderer, stackCountText, i, j, Color.WHITE.toInt(), true);
            context.matrices.pop();
        }
    }

    companion object : Factory {
        override fun register() {
            StackOverlayRegistry.register(::StackCountOverlay, ::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return true;
        }
    }
}