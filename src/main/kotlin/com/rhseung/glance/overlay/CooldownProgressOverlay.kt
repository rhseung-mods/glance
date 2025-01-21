package com.rhseung.glance.overlay

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper

class CooldownProgressOverlay(item: Item, itemStack: ItemStack) : GlanceStackOverlay(item, itemStack) {
    override fun render(
        context: DrawContext,
        textRenderer: TextRenderer,
        x: Int,
        y: Int,
        stackCountText: String?
    ) {
        val cooldown = getCooldown(itemStack);
        val y0 = y + MathHelper.floor(16f * (1f - cooldown));
        val y1 = y + 16;

        context.fill(RenderLayer.getGui(), x, y0, x + 16, y1, 200, Integer.MAX_VALUE);
    }

    companion object : Factory {
        fun getCooldown(itemStack: ItemStack): Float {
            val client = MinecraftClient.getInstance();
            val player = client.player;
            val tickDelta = client.renderTickCounter.getTickDelta(true);
            return player?.itemCooldownManager?.getCooldownProgress(itemStack, tickDelta) ?: 0f;
        }

        override fun register() {
            StackOverlayRegistry.register(::CooldownProgressOverlay, ::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            val cooldown = getCooldown(itemStack);
            return cooldown > 0f;
        }
    }
}