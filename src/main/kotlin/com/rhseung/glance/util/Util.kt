package com.rhseung.glance.util

import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.EnchantmentEffectComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.ItemEnchantmentsComponent
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipData
import net.minecraft.registry.entry.RegistryEntry
import java.util.*

object Util {
    fun Double.roundTo(n: Int) = "%.${n}f".format(this).toDouble();

    fun Float.roundTo(n: Int) = "%.${n}f".format(this).toFloat();

    fun Double.toStringPretty() = this.roundTo(2).toString().removeSuffix("0").removeSuffix(".");

    fun Float.toStringPretty() = this.roundTo(2).toString().removeSuffix("0").removeSuffix(".");

    fun <T> ifElse(condition: Boolean, ifTrue: T, ifFalse: T): T = if (condition) ifTrue else ifFalse;

    /**
     * [net.minecraft.item.Item.getTooltipData]
     */
    fun ItemStack.getTooltipDataWithClient(client: MinecraftClient): Optional<TooltipData> {
        val item = this.item;
        val original = item.getTooltipData(this);
        val compound = TooltipDataFactoryManager.find(item, this, client);
        compound.components = compound.components.filter { it.getHeight(client.textRenderer) > 0 }.toMutableList();

        original.ifPresent { data -> compound.add(0, data) }

        return if (compound.size() > 0) Optional.of(compound) else original;
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