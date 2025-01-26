package com.rhseung.glance.overlay

import com.rhseung.glance.util.Color
import net.minecraft.block.BeehiveBlock
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.BlockStateComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class HoneyLevelOverlay(item: Item, itemStack: ItemStack) : GlanceStackOverlay(item, itemStack) {
    override fun render(
        context: DrawContext,
        textRenderer: TextRenderer,
        x: Int,
        y: Int,
        stackCountText: String?
    ) {
        val blockStateComponent: BlockStateComponent = itemStack.get(DataComponentTypes.BLOCK_STATE)!!;
        val honeyLevel: Int = blockStateComponent.getValue(BeehiveBlock.HONEY_LEVEL) ?: 0;

        val i = x + 1;
        val j = y + 13;

        context.fill(RenderLayer.getGui(), i, j, i + 14, j + 2, 200, Color.BLACK.toInt(150));
        for (level in 0..<honeyLevel) {
            context.fill(RenderLayer.getGui(), i + 3 * level, j, i + 3 * level + 2, j + 1, 200, Color.GOLD.toInt());
        }
    }

    companion object : Factory {
        override fun register() {
            StackOverlayRegistry.register(::HoneyLevelOverlay, ::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return itemStack.contains(DataComponentTypes.BLOCK_STATE) &&
                    (itemStack.get(DataComponentTypes.BLOCK_STATE)!!.getValue(BeehiveBlock.HONEY_LEVEL) ?: 0) > 0;
        }
    }
}