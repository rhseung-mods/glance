package com.rhseung.glance.overlay

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

abstract class GlanceStackOverlay(open val item: Item, open val itemStack: ItemStack) {
    abstract fun render(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int, stackCountText: String? = null);

    interface Factory {
        fun register();

        fun valid(item: Item, itemStack: ItemStack): Boolean;
    }
}