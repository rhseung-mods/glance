package com.rhseung.glance.tooltip.base

import com.rhseung.glance.draw.DrawableTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.util.TooltipBackground
import com.rhseung.glance.tooltip.util.TooltipConstants
import com.rhseung.glance.tooltip.util.TooltipConstants.ITEM_PADDNIG
import com.rhseung.glance.tooltip.util.TooltipConstants.ITEM_SLOT_SIZE
import com.rhseung.glance.util.Color.Companion.toColor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class ModelTooltip(val data: ModelTooltipData) : AbstractTooltip<ModelTooltip.ModelTooltipData>(data) {
    class ModelTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) : AbstractTooltipData(item, stack, client) {
        override fun getTooltip(): DrawableTooltip {
            return DrawableTooltip();
        }
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return ITEM_PADDNIG + ITEM_SLOT_SIZE + ITEM_PADDNIG;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return ITEM_PADDNIG + ITEM_SLOT_SIZE + ITEM_PADDNIG;
    }

    override fun drawItems(
        textRenderer: TextRenderer,
        x0: Int,
        y0: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        val z = 400;

        TooltipBackground.render(context,
            x0 + ITEM_PADDNIG, y0 + ITEM_PADDNIG,
            ITEM_SLOT_SIZE, ITEM_SLOT_SIZE,
            z, data.stack.rarity.formatting.toColor()
        );

        context.drawItem(data.stack,
            x0 + ITEM_PADDNIG + ITEM_SLOT_SIZE / 2,
            y0 + ITEM_PADDNIG + ITEM_SLOT_SIZE / 2
        );
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<ModelTooltipData>(::ModelTooltip);
        }
    }
}