package com.rhseung.glance.tooltip

import com.rhseung.glance.draw.DrawableTooltip
import com.rhseung.glance.draw.DrawableLine
import com.rhseung.glance.draw.element.icon.HudIcon
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.draw.element.icon.TooltipIcon
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.consume.ApplyEffectsConsumeEffect
import kotlin.math.ceil

class FoodTooltip(data: FoodTooltipData) : AbstractTooltip<FoodTooltip.FoodTooltipData>(data) {
    class FoodTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) : AbstractTooltipData(item, stack, client) {
        private val food = item.components[DataComponentTypes.FOOD]!!;
        val hunger = food.nutrition;
        val saturation = food.saturation;
        val hungerIconCount = ceil(hunger / 2.0).toInt();
        val saturationIconCount = ceil(saturation / 2.0).toInt();
        val foodIcon: TooltipIcon;
        val saturationIcon: TooltipIcon;

        init {
            if (stack.contains(DataComponentTypes.CONSUMABLE)) {
                val hasHunger = stack.get(DataComponentTypes.CONSUMABLE)!!.onConsumeEffects().any { consumeEffect ->
                    val applyEffectsConsumeEffect = (consumeEffect as? ApplyEffectsConsumeEffect) ?: return@any false;
                    applyEffectsConsumeEffect.effects.any { it.effectType == StatusEffects.HUNGER }
                };

                foodIcon = if (hasHunger) TooltipIcon.FOOD_HUNGER else TooltipIcon.FOOD;
                saturationIcon = if (hasHunger) TooltipIcon.SATURATION_HUNGER else TooltipIcon.SATURATION;
            } else {
                foodIcon = TooltipIcon.FOOD;
                saturationIcon = TooltipIcon.SATURATION;
            }
        }

        override fun getTooltip(): DrawableTooltip {
            var hungerTooltip = DrawableLine();
            for (i in 0..<hungerIconCount) {
                if (i == hungerIconCount - 1)
                    hungerTooltip += foodIcon[hunger % 2];
                else
                    hungerTooltip += foodIcon;
            }
    
            var saturationTooltip = DrawableLine();
            for (i in 0..<saturationIconCount) {
                if (i == saturationIconCount - 1)
                    saturationTooltip += saturationIcon[ceil((saturation % 2) / 2 * 3).toInt()];
                else
                    saturationTooltip += saturationIcon;
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