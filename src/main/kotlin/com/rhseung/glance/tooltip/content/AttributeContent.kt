package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.TooltipConstants
import com.rhseung.glance.tooltip.TooltipConstants.Padding.BETWEEN_SIGN_VALUE
import com.rhseung.glance.tooltip.TooltipConstants.Padding.NEXT_ICON
import com.rhseung.glance.tooltip.TooltipConstants.Padding.SLOT_MARGIN
import com.rhseung.glance.tooltip.TooltipConstants.Padding.SPACE
import com.rhseung.glance.tooltip.component.*
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.toColor
import com.rhseung.glance.util.Color.Companion.with
import com.rhseung.glance.util.Slot
import com.rhseung.glance.util.Slot.Companion.toSlot
import com.rhseung.glance.util.Util.toStringPretty
import com.rhseung.glance.tooltip.icon.AttributeIcon.Companion.toIcon
import com.rhseung.glance.tooltip.icon.SignIcon
import com.rhseung.glance.tooltip.icon.SignIcon.Companion.toSignIcon
import com.rhseung.glance.tooltip.icon.SlotIcon.Companion.toIcon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.EnchantmentEffectComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.ItemEnchantmentsComponent
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.entry.RegistryEntry
import java.util.SortedMap
import kotlin.math.abs

class AttributeContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    private val attributeModifiers = item.components.getOrDefault(
        DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT
    ).modifiers;
    private val player = MinecraftClient.getInstance().player;
    private val lines: SortedMap<Slot, LineComponent> = sortedMapOf(compareBy(Slot::ordinal));

    private fun add(slot: Slot, line: LineComponent?) {
        if (line == null) return;

        if (slot !in lines)
            lines.putIfAbsent(slot, line);
        else
            lines[slot]!!.add(XPaddingComponent(NEXT_ICON)).add(line);
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
     * @see net.minecraft.enchantment.EnchantmentHelper.applyAttributeModifiers
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
     * @see net.minecraft.component.type.PotionContentsComponent.buildTooltip
     */
    fun potionAttribute(
        stack: ItemStack,
        attributeModifierConsumer: (RegistryEntry<EntityAttribute>, EntityAttributeModifier) -> Unit
    ) {
        val potionContentsComponent = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);

        potionContentsComponent.effects.forEach { effectInstance ->
            effectInstance.effectType.value().forEachAttributeModifier(effectInstance.amplifier, attributeModifierConsumer)
        }
    }

    /**
     * @see net.minecraft.item.ItemStack.appendAttributeModifierTooltip
     */
    private fun attributeTooltip(
        attribute: RegistryEntry<EntityAttribute>,
        modifier: EntityAttributeModifier,
        blTrueAttributes: List<RegistryEntry<EntityAttribute>>
    ): LineComponent? {
        var value = modifier.value;
        var isFixed = false;

        if (player != null && blTrueAttributes.find { it.matches(attribute) } != null) {
            value += player.getAttributeBaseValue(attribute);
            isFixed = true;
        }

        if (value == 0.0)
            return null;

        var isMultiplier = false;
        if (modifier.operation != Operation.ADD_MULTIPLIED_BASE && modifier.operation != Operation.ADD_MULTIPLIED_TOTAL) {
            if (attribute.matches(EntityAttributes.KNOCKBACK_RESISTANCE))
                value *= 10;
        } else {
            isMultiplier = true;
        }

        val signIcon: SignIcon? = attribute.value().toSignIcon(value);
        val color: Color = if (!isFixed) attribute.value().getFormatting(value > 0).toColor() else Color.WHITE;
        val content: String = if (!isFixed) abs(value).toStringPretty() else value.toStringPretty();
        val text = (content + if (isMultiplier) TooltipConstants.Char.MULTIPLY else "") with color;

        val ret = LineComponent(
            IconComponent(attribute.toIcon()),
            XPaddingComponent(SPACE)
        );

        if (!isFixed && signIcon != null) {
            ret.add(IconComponent(signIcon))
                .add(XPaddingComponent(BETWEEN_SIGN_VALUE));
        }

        return ret.add(TextComponent(text, shift = 1));
    }

    override fun getComponents(): List<LineComponent> {
        for (slot in AttributeModifierSlot.entries) {
            defaultAttribute(attributeModifiers, slot) { attribute, modifier ->
                val blTrueAttributes = listOf(
                    EntityAttributes.ATTACK_DAMAGE,
                    EntityAttributes.ATTACK_SPEED,
                    EntityAttributes.ARMOR,
                    EntityAttributes.ARMOR_TOUGHNESS,
                    EntityAttributes.KNOCKBACK_RESISTANCE
                );

                this.add(slot.toSlot(), attributeTooltip(attribute, modifier, blTrueAttributes));
            }

            enchantmentAttribute(itemStack, slot) { attribute, modifier ->
                this.add(slot.toSlot(), attributeTooltip(attribute, modifier, listOf()));
            }
        }

        potionAttribute(itemStack) { attribute, modifier ->
            this.add(Slot.DRANK, attributeTooltip(attribute, modifier, listOf()));
        }

        if (lines.size > 1) {
            lines.forEach { (slot, line) ->
                line.addAll(0, LineComponent(
                    IconComponent(slot.toIcon()),
                    XPaddingComponent(SPACE),
                    TextComponent(">"),
                    XPaddingComponent(SLOT_MARGIN),
                ))
            };
        }

        return lines.sequencedValues().toList();
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::AttributeContent, AttributeContent::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return !Screen.hasShiftDown();
        }
    }
}