package com.rhseung.glance.util

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.EnchantmentEffectComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.ItemEnchantmentsComponent
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.math.abs
import kotlin.math.ceil

object Util {
    fun Double.roundTo(n: Int) = "%.${n}f".format(this).toDouble();

    fun Float.roundTo(n: Int) = "%.${n}f".format(this).toFloat();

    fun Double.toStringPretty() = this.roundTo(2).toString().removeSuffix("0").removeSuffix(".");

    fun Float.toStringPretty() = this.roundTo(2).toString().removeSuffix("0").removeSuffix(".");

    // TODO: x0Hover..(x0Hover + tooltipWidth) => x0Hover with tooltipWidth
    fun Int.toRangeSize(size: Int) = this..<(this + size);

    fun IntRange.modify(startDelta: Int, endDelta: Int) = (this.first + startDelta)..(this.last + endDelta);

    fun IntRange.size() = this.endExclusive - this.first;

    fun IntProgression.size() = abs((this.last - this.first) / this.step) + 1;

    fun IntRange.splitToTwo(): Pair<IntRange, IntRange> {
        val mid = (this.first + this.last) / 2;
        return this.first..mid to (mid + 1)..this.last;
    }

    fun <T> Boolean.ifElse(ifTrue: T, ifFalse: T): T = if (this) ifTrue else ifFalse;

    fun Boolean.toInt() = if (this) 1 else 0;

    inline fun <T, R> Iterable<T>.mapRight(transform: (T) -> R): List<R> {
        val list = mutableListOf<R>()
        for (item in this.reversed()) {
            list.add(0, transform(item));
        }
        return list;
    }

    inline fun <T, R> Iterable<T>.forEachRight(action: (T) -> R) {
        for (item in this.reversed()) {
            action(item);
        }
    }

    fun <T> Iterable<T>.joinTo(separator: T): List<T> {
        val list = mutableListOf<T>()
        for (item in this) {
            list.add(item);
            list.add(separator);
        }
        if (list.isNotEmpty())
            list.removeLast();
        return list;
    }

    fun ceilToInt(value: Float) = ceil(value).toInt();

    fun ceilToInt(value: Double) = ceil(value).toInt();

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
            @Suppress("UNCHECKED_CAST")
            field[receiver] as T
        } catch (e: IllegalAccessException) {
            throw e
        } catch (e: ClassCastException) {
            throw e
        }
    }

    fun <T> Any.getProperty(propertyName: String): T {
        return get(this, propertyName);
    }

    @Throws(NoSuchMethodException::class)
    private fun getMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method {
        val methods = clazz.declaredMethods.filter { it.name == methodName && it.parameterCount == parameterTypes.size };

        if (methods.isEmpty()) {
            val superClass = clazz.superclass;
            if (superClass == null)
                throw NoSuchMethodException();
            else
                return getMethod(superClass, methodName, *parameterTypes);
        }

        if (methods.size == 1)
            return methods[0];

        for (method in methods) {
            if (method.parameterTypes.contentEquals(parameterTypes))
                return method;
        }

        throw NoSuchMethodException();
    }

    fun <T> invoke(receiver: Any, methodName: String, vararg args: Any?): T {
        val clazz: Class<*> = receiver.javaClass

        // Determine the parameter types of the method
        val parameterTypes = args.map { it?.javaClass ?: Any::class.java }.toTypedArray()

        val method: Method
        try {
            method = getMethod(clazz, methodName, *parameterTypes)
        } catch (e: NoSuchMethodException) {
            throw e
        }

        method.isAccessible = true

        return try {
            @Suppress("UNCHECKED_CAST")
            method.invoke(receiver, *args) as T
        } catch (e: IllegalAccessException) {
            throw e
        } catch (e: ClassCastException) {
            throw e
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }

    fun <T> Any.invokeMethod(methodName: String, vararg args: Any?): T {
        return invoke(this, methodName, *args)
    }

    fun OrderedText.toText(): Text {
        val mutableTextArr = mutableListOf(Text.empty());
        this.accept { idx, style, c ->
            mutableTextArr[0] = mutableTextArr[0].append(Text.literal(Character.toString(c)).setStyle(style));
            true;
        }
        return mutableTextArr[0];
    }

    fun <E> List<E>.safeGet(idx: Int): E? {
        return if (idx in this.indices) this[idx] else null;
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

    fun getEquipmentSlot(player: PlayerEntity, stack: ItemStack): EquipmentSlot? {
        return EquipmentSlot.VALUES.find { slot -> player.getEquippedStack(slot) == stack };
    }
}