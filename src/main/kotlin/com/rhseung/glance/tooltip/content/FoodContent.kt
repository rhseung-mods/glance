package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.icon.TooltipIcon
import com.rhseung.glance.tooltip.component.IconComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.StackedComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.with
import com.rhseung.glance.util.Util.ceilToInt
import com.rhseung.glance.util.Util.toStringPretty
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffectUtil
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.consume.ApplyEffectsConsumeEffect
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class FoodContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    private val foodComponent = item.components[DataComponentTypes.FOOD]!!;
    private val food = foodComponent.nutrition;
    private val saturation = foodComponent.saturation;
    private val foodIconCount = ceilToInt(food / 2.0);
    private val saturationIconCount = ceilToInt(saturation / 2.0);
    private val foodIcon: TooltipIcon;
    private val saturationIcon: TooltipIcon;

    private fun getEffects(itemStack: ItemStack): List<ApplyEffectsConsumeEffect> {
        val consumable = itemStack.get(DataComponentTypes.CONSUMABLE) ?: return emptyList();
        return consumable.onConsumeEffects().filterIsInstance<ApplyEffectsConsumeEffect>();
    }

    private fun hasEffect(itemStack: ItemStack, effectType: RegistryEntry<StatusEffect>): Boolean {
        return effectType in getEffects(itemStack).flatMap { it.effects().map(StatusEffectInstance::getEffectType) };
    }

    init {
        val hasHunger = hasEffect(itemStack, StatusEffects.HUNGER);

        foodIcon = if (hasHunger) TooltipIcon.FOOD_HUNGER else TooltipIcon.FOOD;
        saturationIcon = if (hasHunger) TooltipIcon.SATURATION_HUNGER else TooltipIcon.SATURATION;
    }

    private fun getFoodIcon(idx: Int): TooltipIcon {
        return if (idx == foodIconCount - 1)
            foodIcon[food % 2]
        else
            foodIcon;
    }

    private fun getSaturationIcon(idx: Int): TooltipIcon {
        return if (idx == saturationIconCount - 1)
            saturationIcon[ceilToInt((saturation % 2) / 2 * 3)]
        else
            saturationIcon;
    }

    override fun getComponents(): List<LineComponent> {
        val icons = LineComponent();

        for (i in 0..<maxOf(foodIconCount, saturationIconCount)) {
            val foodIconComponent = if (i < foodIconCount) IconComponent(getFoodIcon(i)) else null;
            val saturationIconComponent = if (i < saturationIconCount) IconComponent(getSaturationIcon(i)) else null;

            if (i < foodIconCount && i < saturationIconCount)
                icons.add(StackedComponent(foodIconComponent!!, saturationIconComponent!!));
            else if (i < foodIconCount)
                icons.add(foodIconComponent!!);
            else if (i < saturationIconCount)
                icons.add(saturationIconComponent!!);
        }

        return listOf(icons);
    }

    override fun getShiftComponents(): List<LineComponent> {
        // translate: "음식: "
        return listOf(
            LineComponent(TextComponent(("Food: " with Color.GRAY).append(food.toString() with Color.WHITE))),
            LineComponent(TextComponent(("Saturation: " with Color.GRAY).append(saturation.toStringPretty() with Color.WHITE))),
        );
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