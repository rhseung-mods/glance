package com.rhseung.glance.tooltip

import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.icon.Icon
import com.rhseung.glance.icon.TooltipIcon
import com.rhseung.glance.util.Draw
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import kotlin.math.ceil

class FoodTooltip(override val data: FoodTooltipData) : AbstractTooltip(data) {
    class FoodTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) : AbstractTooltipData(item, stack, client) {
        private val food = item.components[DataComponentTypes.FOOD]!!;
        val hunger = food.nutrition;
        val saturation = food.saturation;
        val hungerIconCount = ceil(hunger / 2.0).toInt();
        val saturationIconCount = ceil(saturation / 2.0).toInt();
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return Icon.HEIGHT + Draw.LINE_MARGIN;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return Icon.WIDTH * maxOf(data.hungerIconCount, data.saturationIconCount);
    }

    override fun drawItems(
        textRenderer: TextRenderer,
        x0: Int,
        y0: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        var x = x0;
        for (i in 0..<data.hungerIconCount) {
            x = x0 + i * Icon.WIDTH;

            if (i == data.hungerIconCount - 1)
                TooltipIcon.HUNGER.draw(context, x, y0, data.hunger % 2);
            else
                TooltipIcon.HUNGER.draw(context, x, y0);
        }

        x = x0;
        for (i in 0..<data.saturationIconCount) {
            x = x0 + i * Icon.WIDTH;

            if (i == data.saturationIconCount - 1)
                TooltipIcon.SATURATION.draw(context, x, y0, ceil((data.saturation % 2) / 2 * 3).toInt());
            else
                TooltipIcon.SATURATION.draw(context, x, y0);
        }
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<FoodTooltipData>(::FoodTooltip);
            TooltipDataFactoryManager.set<Item> { item, stack, client ->
                if (item.components[DataComponentTypes.FOOD] != null)
                    FoodTooltipData(item, stack, client);
                else
                    null;
            };
        }
    }
}