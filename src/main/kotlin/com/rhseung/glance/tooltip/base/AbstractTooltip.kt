package com.rhseung.glance.tooltip.base

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipData

abstract class AbstractTooltip(open val data: AbstractTooltipData) : TooltipComponent {
    abstract class AbstractTooltipData(open val item: Item, open val stack: ItemStack, open val client: MinecraftClient) : TooltipData

    abstract override fun getHeight(textRenderer: TextRenderer): Int

    abstract override fun getWidth(textRenderer: TextRenderer): Int

    abstract override fun drawItems(textRenderer: TextRenderer, x0: Int, y0: Int, width: Int, height: Int, context: DrawContext)
}