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
import net.minecraft.client.network.ClientPlayerEntity
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
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.SortedMap
import kotlin.math.abs

class AttributeContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    private val player: ClientPlayerEntity? = MinecraftClient.getInstance().player;
    private val lines: SortedMap<Slot, LineComponent> = sortedMapOf(compareBy(Slot::ordinal));
    private val texts: SortedMap<Slot, MutableList<TextComponent>> = sortedMapOf(compareBy(Slot::ordinal));

    private fun addIcon(slot: Slot, line: LineComponent?) {
        if (line == null) return;

        if (slot !in lines)
            lines.putIfAbsent(slot, line);
        else
            lines[slot]!!.add(XPaddingComponent(NEXT_ICON)).add(line);
    }

    private fun addText(slot: Slot, text: TextComponent?) {
        if (text == null) return;

        if (slot !in texts) {
            texts.putIfAbsent(slot, mutableListOf());
            texts[slot]!!.add(TextComponent(Text.translatable(slot.translationKey).formatted(Formatting.GRAY)));
        }

        texts[slot]!!.add(text);
    }

    fun defaultAttribute(
        stack: ItemStack,
        slot: AttributeModifierSlot,
        attributeModifierConsumer: (RegistryEntry<EntityAttribute>, EntityAttributeModifier) -> Unit
    ) {
        val attributeModifiers = stack
            .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        attributeModifiers.modifiers.forEach { entry ->
            if (entry.slot.equals(slot)) {
                attributeModifierConsumer(entry.attribute, entry.modifier);
            }
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
        val itemEnchantmentsComponent = stack
            .getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

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
        val potionContentsComponent = stack
            .getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);

        potionContentsComponent.effects.forEach { effectInstance ->
            effectInstance.effectType.value()
                .forEachAttributeModifier(effectInstance.amplifier, attributeModifierConsumer)
        }
    }

    /**
     * @see net.minecraft.item.ItemStack.appendAttributeModifierTooltip
     */
    private fun attributeIconTooltip(
        attribute: RegistryEntry<EntityAttribute>,
        modifier: EntityAttributeModifier,
        basedAttributes: List<RegistryEntry<EntityAttribute>>
    ): LineComponent? {
        var value: Double = modifier.value;
        var isFixed = false;

        if (player != null && basedAttributes.find { it.matches(attribute) } != null) {
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

        return ret.add(TextComponent(text, shiftY = 1));
    }

    /**
     * @see net.minecraft.item.ItemStack.appendAttributeModifierTooltip
     */
    private fun attributeTextTooltip(
        attribute: RegistryEntry<EntityAttribute>,
        modifier: EntityAttributeModifier,
        basedAttributes: List<RegistryEntry<EntityAttribute>>
    ): TextComponent? {
        var value: Double = modifier.value;
        var isFixed = false;

        if (player != null && basedAttributes.find { it.matches(attribute) } != null) {
            value += player.getAttributeBaseValue(attribute);
            isFixed = true;
        }

        if (value == 0.0)
            return null;

        if (modifier.operation == Operation.ADD_MULTIPLIED_BASE || modifier.operation == Operation.ADD_MULTIPLIED_TOTAL)
            value *= 100;
        else if (attribute.matches(EntityAttributes.KNOCKBACK_RESISTANCE))
            value *= 10;

        val text: MutableText = if (isFixed) {
            ScreenTexts.space().append(
                Text.translatable(
                    "attribute.modifier.equals." + modifier.operation.id,
                    AttributeModifiersComponent.DECIMAL_FORMAT.format(value),
                    Text.translatable(attribute.value().translationKey)
                ).formatted(Formatting.DARK_GREEN)
            )
        }
        else if (value > 0) {
            Text.translatable(
                "attribute.modifier.plus." + modifier.operation.id,
                AttributeModifiersComponent.DECIMAL_FORMAT.format(value),
                Text.translatable(attribute.value().translationKey)
            ).formatted(attribute.value().getFormatting(true))
        }
        else if (value < 0) {
            Text.translatable(
                "attribute.modifier.take." + modifier.operation.id,
                AttributeModifiersComponent.DECIMAL_FORMAT.format(-value),
                Text.translatable(attribute.value().translationKey)
            ).formatted(attribute.value().getFormatting(false))
        }
        else throw IllegalStateException("impossible");

        return TextComponent(text);
    }

    override fun getComponents(): List<LineComponent> {
        for (slot in AttributeModifierSlot.entries) {
            defaultAttribute(itemStack, slot) { attribute, modifier ->
                val basedAttributes = listOf(
                    EntityAttributes.ATTACK_DAMAGE,
                    EntityAttributes.ATTACK_SPEED,
                    EntityAttributes.ARMOR,
                    EntityAttributes.ARMOR_TOUGHNESS,
                    EntityAttributes.KNOCKBACK_RESISTANCE
                );

                this.addIcon(slot.toSlot(), attributeIconTooltip(attribute, modifier, basedAttributes));
            }

            enchantmentAttribute(itemStack, slot) { attribute, modifier ->
                this.addIcon(slot.toSlot(), attributeIconTooltip(attribute, modifier, listOf()));
            }
        }

        potionAttribute(itemStack) { attribute, modifier ->
            this.addIcon(Slot.DRANK, attributeIconTooltip(attribute, modifier, listOf()));
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

    override fun getShiftComponents(): List<LineComponent> {
        for (slot in AttributeModifierSlot.entries) {
            defaultAttribute(itemStack, slot) { attribute, modifier ->
                val basedAttributes = listOf(
                    EntityAttributes.ATTACK_DAMAGE,
                    EntityAttributes.ATTACK_SPEED,
                    EntityAttributes.ARMOR,
                    EntityAttributes.ARMOR_TOUGHNESS,
                    EntityAttributes.KNOCKBACK_RESISTANCE
                );

                this.addText(slot.toSlot(), attributeTextTooltip(attribute, modifier, basedAttributes));
            }

            enchantmentAttribute(itemStack, slot) { attribute, modifier ->
                this.addText(slot.toSlot(), attributeTextTooltip(attribute, modifier, listOf()));
            }
        }

        potionAttribute(itemStack) { attribute, modifier ->
            this.addText(Slot.DRANK, attributeTextTooltip(attribute, modifier, listOf()));
        }

        val ret = mutableListOf<TextComponent>();
        texts.forEach { (_, textComponents) ->
            ret.addAll(textComponents);
            ret.add(TextComponent(ScreenTexts.EMPTY));
        }

        return ret.slice(0..<ret.size - 1).map { LineComponent(it) };
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::AttributeContent, AttributeContent::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return true;
        }
    }
}