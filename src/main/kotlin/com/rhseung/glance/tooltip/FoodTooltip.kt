package com.rhseung.glance.tooltip

import com.rhseung.glance.draw.DrawableTooltip
import com.rhseung.glance.draw.DrawableLine
import com.rhseung.glance.draw.element.Padding
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.draw.element.icon.TooltipIcon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import kotlin.math.ceil

class FoodTooltip(data: FoodTooltipData) : AbstractTooltip<FoodTooltip.FoodTooltipData>(data) {
    class FoodTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) : AbstractTooltipData(item, stack, client) {
        private val food = item.components[DataComponentTypes.FOOD]!!;
        val hunger = food.nutrition;
        val saturation = food.saturation;
        val hungerIconCount = ceil(hunger / 2.0).toInt();
        val saturationIconCount = ceil(saturation / 2.0).toInt();

        override fun getTooltip(): DrawableTooltip {
            var hungerTooltip = DrawableLine();
            for (i in 0..<hungerIconCount) {
                if (i == hungerIconCount - 1)
                    hungerTooltip += TooltipIcon.HUNGER[hunger % 2];
                else
                    hungerTooltip += TooltipIcon.HUNGER;
            }
    
            var saturationTooltip = DrawableLine();
            for (i in 0..<saturationIconCount) {
                if (i == saturationIconCount - 1)
                    saturationTooltip += TooltipIcon.SATURATION[ceil((saturation % 2) / 2 * 3).toInt()];
                else
                    saturationTooltip += TooltipIcon.SATURATION;
            }

            return DrawableTooltip(hungerTooltip, saturationTooltip);
        }
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<FoodTooltipData>(::FoodTooltip);
            TooltipDataFactoryManager.set<Item> { item, stack, client ->
                if (DataComponentTypes.FOOD in item.components)
                    FoodTooltipData(item, stack, client);
                else
                    null;
            };
        }
    }
}