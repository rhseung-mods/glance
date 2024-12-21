package com.rhseung.glance.tooltip.content

import com.rhseung.glance.util.icon.TooltipIcon
import com.rhseung.glance.tooltip.component.GlanceTooltipComponent
import com.rhseung.glance.tooltip.component.IconComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.util.Util.ceilToInt
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.consume.ApplyEffectsConsumeEffect

class FoodContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    private val foodComponent = item.components[DataComponentTypes.FOOD]!!;
    private val food = foodComponent.nutrition;
    private val saturation = foodComponent.saturation;
    private val foodIconCount = ceilToInt(food / 2.0);
    private val saturationIconCount = ceilToInt(saturation / 2.0);
    private val foodIcon: TooltipIcon;
    private val saturationIcon: TooltipIcon;

    init {
        if (itemStack.contains(DataComponentTypes.CONSUMABLE)) {
            val hasHunger = itemStack.get(DataComponentTypes.CONSUMABLE)!!.onConsumeEffects().any { consumeEffect ->
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

    override fun getComponents(): List<GlanceTooltipComponent> {
        val foodLine = LineComponent();
        for (i in 0..<foodIconCount) {
            if (i == foodIconCount - 1)
                foodLine.add(IconComponent(foodIcon[food % 2]));
            else
                foodLine.add(IconComponent(foodIcon));
        }

        val saturationLine = LineComponent();
        for (i in 0..<saturationIconCount) {
            if (i == saturationIconCount - 1)
                saturationLine.add(IconComponent(saturationIcon[ceilToInt((saturation % 2) / 2 * 3)]));
            else
                saturationLine.add(IconComponent(saturationIcon));
        }

        return listOf(foodLine, saturationLine);
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::FoodContent, FoodContent::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return item.components.contains(DataComponentTypes.FOOD);
        }
    }
}