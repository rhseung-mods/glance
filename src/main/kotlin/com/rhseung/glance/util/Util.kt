package com.rhseung.glance.util

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.EnchantmentEffectComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.ItemEnchantmentsComponent
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.registry.entry.RegistryEntry
import java.lang.reflect.Field

object Util {
    fun Double.roundTo(n: Int) = "%.${n}f".format(this).toDouble();

    fun Float.roundTo(n: Int) = "%.${n}f".format(this).toFloat();

    fun Double.toStringPretty() = this.roundTo(2).toString().removeSuffix("0").removeSuffix(".");

    fun Float.toStringPretty() = this.roundTo(2).toString().removeSuffix("0").removeSuffix(".");

    // TODO: x0Hover..(x0Hover + tooltipWidth) => x0Hover with tooltipWidth
    fun Int.toRangeSize(size: Int) = this..<(this + size);

    fun IntRange.modify(startDelta: Int, endDelta: Int) = (this.start + startDelta)..(this.endInclusive + endDelta);

    fun IntRange.size() = this.endExclusive - this.start;

    fun IntRange.splitToTwo(): Pair<IntRange, IntRange> {
        val mid = (this.start + this.endInclusive) / 2;
        return this.start..mid to (mid + 1)..this.endInclusive;
    }

    fun <T> ifElse(condition: Boolean, ifTrue: T, ifFalse: T): T = if (condition) ifTrue else ifFalse;

    @Throws(NoSuchFieldException::class)
    private fun getField(clazz: Class<*>, fieldName: String): Field {
        return try {
            clazz.getDeclaredField(fieldName);
        } catch (e: NoSuchFieldException) {
            val superClass = clazz.superclass;
            if (superClass == null) {
                throw e;
            } else {
                getField(superClass, fieldName);
            }
        }
    }

    fun <T> Any.getProperty(propertyName: String): T {
        return get(this, propertyName);
    }

    fun <T> get(receiver: Any, propertyName: String): T {
        val clazz: Class<*> = receiver.javaClass

        var field: Field? = null
        try {
            field = getField(clazz, propertyName)
        } catch (e: NoSuchFieldException) {
            throw e
        }

        field.isAccessible = true

        return try {
            field[receiver] as T
        } catch (e: IllegalAccessException) {
            throw e
        } catch (e: ClassCastException) {
            throw e
        }
    }

    fun defaultAttribute(
        attributeModifiers: List<AttributeModifiersComponent.Entry>,
        slot: AttributeModifierSlot,
        attributeModifierConsumer: (RegistryEntry<EntityAttribute>, EntityAttributeModifier) -> Unit
    ) {
        attributeModifiers.forEach { entry ->
            if (!entry.slot.equals(slot))
                return@forEach;

            attributeModifierConsumer(entry.attribute, entry.modifier);
        }
    }

    /**
     * [net.minecraft.enchantment.EnchantmentHelper.applyAttributeModifiers]
     */
    fun enchantmentAttribute(
        stack: ItemStack,
        slot: AttributeModifierSlot,
        attributeModifierConsumer: (RegistryEntry<EntityAttribute>, EntityAttributeModifier) -> Unit
    ) {
        /**
         * [net.minecraft.enchantment.EnchantmentHelper.forEachEnchantment]
         */
        val itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        itemEnchantmentsComponent.enchantmentEntries.forEach { (enchantment, level) ->
            enchantment.value().getEffect(EnchantmentEffectComponentTypes.ATTRIBUTES).forEach { effect ->
                if (enchantment.value().definition().slots().contains(slot)) {
                    attributeModifierConsumer(effect.attribute(), effect.createAttributeModifier(level, slot));
                }
            }
        }
    }

    /**
     * [net.minecraft.component.type.PotionContentsComponent.buildTooltip]
     */
    fun potionAttribute(
        stack: ItemStack,
        attributeModifierConsumer: (RegistryEntry<EntityAttribute>, EntityAttributeModifier) -> Unit
    ) {
        val potionContentsComponent = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
        potionContentsComponent.effects.forEach { it.effectType.value().forEachAttributeModifier(it.amplifier, attributeModifierConsumer) }
    }
}