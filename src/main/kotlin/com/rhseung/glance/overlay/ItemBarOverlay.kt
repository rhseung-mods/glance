package com.rhseung.glance.overlay

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.ColorHelper

class ItemBarOverlay(item: Item, itemStack: ItemStack) : GlanceStackOverlay(item, itemStack) {
    override fun render(
        context: DrawContext,
        textRenderer: TextRenderer,
        x: Int,
        y: Int,
        stackCountText: String?
    ) {
        val i = x + 2;
        val j = y + 13;

        context.fill(RenderLayer.getGui(),
            i, j, i + itemStack.itemBarStep, j + 1,
            200, ColorHelper.withAlpha(150, itemStack.itemBarColor)
        );
        context.fill(RenderLayer.getGui(),
            i + itemStack.itemBarStep, j, i + 13, j + 1,
            200, ColorHelper.withAlpha(150, -16777216)
        );
        context.fill(RenderLayer.getGui(),
            i, j + 1, i + 13, j + 2,
            200, ColorHelper.withAlpha(150, -16777216)
        );
    }

    companion object : Factory {
        override fun register() {
            StackOverlayRegistry.register(::ItemBarOverlay, ::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return itemStack.isItemBarVisible;
        }
    }
}