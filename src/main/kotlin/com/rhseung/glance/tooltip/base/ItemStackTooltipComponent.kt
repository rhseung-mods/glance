package com.rhseung.glance.tooltip.base

import com.rhseung.glance.draw.DrawHelper
import com.rhseung.glance.tooltip.util.TooltipConstants
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.toRangeSize
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.item.ItemStack

class ItemStackTooltipComponent(val stack: ItemStack, val color: Color) : TooltipComponent {
    override fun getHeight(textRenderer: TextRenderer): Int {
        return TooltipConstants.MODEL_FRAME + TooltipConstants.ITEM_SLOT_SIZE + TooltipConstants.MODEL_FRAME;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return TooltipConstants.MODEL_FRAME + TooltipConstants.ITEM_SLOT_SIZE + TooltipConstants.MODEL_FRAME;
    }

    override fun drawItems(
        textRenderer: TextRenderer,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        context.matrices.push();
        DrawHelper.drawSprite(context, DrawHelper.BACKGROUND,
            x.toRangeSize(getWidth(textRenderer)),
            y.toRangeSize(getHeight(textRenderer)),
            400, color
        );
        DrawHelper.drawBorder(context,
            x.toRangeSize(getWidth(textRenderer)),
            y.toRangeSize(getHeight(textRenderer)),
            400, color.darker(0.6f)
        );
        DrawHelper.drawItem(context, stack,
            x + TooltipConstants.MODEL_FRAME,
            y + TooltipConstants.MODEL_FRAME,
            400
        );
        context.matrices.pop();
    }
}