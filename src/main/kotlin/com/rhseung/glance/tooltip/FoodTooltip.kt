package com.rhseung.glance.tooltip

import com.rhseung.glance.draw.DrawableGroup
import com.rhseung.glance.draw.DrawableLine
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.draw.Icon
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

        fun getTooltip(): DrawableGroup {
            var hungerTooltip = DrawableLine();
            for (i in 0..<hungerIconCount) {
                if (i == hungerIconCount - 1)
                    hungerTooltip += TooltipIcon.HUNGER[hunger % 2];
                else
                    hungerTooltip += TooltipIcon.HUNGER[0];
            }

            var saturationTooltip = DrawableLine();
            for (i in 0..<saturationIconCount) {
                if (i == saturationIconCount - 1)
                    saturationTooltip += TooltipIcon.SATURATION[ceil((saturation % 2) / 2 * 3).toInt()];
                else
                    saturationTooltip += TooltipIcon.SATURATION[0];
            }

            return DrawableGroup(hungerTooltip, saturationTooltip);
        }
    }

    val tooltip = data.getTooltip();

    override fun getHeight(textRenderer: TextRenderer): Int {
        return tooltip.getHeight(textRenderer);
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return tooltip.getWidth(textRenderer);
    }

    override fun drawItems(
        textRenderer: TextRenderer,
        x0: Int,
        y0: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        tooltip.draw(context, textRenderer, x0, y0);
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