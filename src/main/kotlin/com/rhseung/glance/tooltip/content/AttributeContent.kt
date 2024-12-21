package com.rhseung.glance.tooltip.content

import com.rhseung.glance.draw.element.GlanceText.Companion.with
import com.rhseung.glance.draw.element.Padding
import com.rhseung.glance.draw.element.icon.AttributeIcon.Companion.toIcon
import com.rhseung.glance.draw.element.icon.SignIcon
import com.rhseung.glance.draw.element.icon.SignIcon.Companion.toSignIcon
import com.rhseung.glance.draw.element.icon.SlotIcon.Companion.toIcon
import com.rhseung.glance.legacy_tooltip.util.SpecialChar
import com.rhseung.glance.tooltip.component.GlanceTooltipComponent
import com.rhseung.glance.tooltip.component.IconComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.tooltip.component.XPaddingComponent
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.toColor
import com.rhseung.glance.util.Slot
import com.rhseung.glance.util.Slot.Companion.toSlot
import com.rhseung.glance.util.Util
import com.rhseung.glance.util.Util.toStringPretty
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.entry.RegistryEntry
import kotlin.math.abs

class AttributeContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    private val attributeModifiers = item.components.getOrDefault(
        DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT
    ).modifiers;
    private val player = MinecraftClient.getInstance().player;
    private val lines: MutableMap<Slot, LineComponent> = mutableMapOf();

    private fun add(slot: Slot, line: LineComponent?) {
        if (line == null) return;

        if (slot !in lines)
            lines[slot] = line;
        else
            lines[slot]!!.add(XPaddingComponent(Padding.NEXT_ICON.size)).add(line);
    }

    /**
     * [net.minecraft.item.ItemStack.appendAttributeModifierTooltip]
     */
    private fun eachAttributeTooltip(
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
        val text = (content + if (isMultiplier) SpecialChar.MULTIPLY else "") with color;

        val ret = LineComponent(
            IconComponent(attribute.toIcon()),
            XPaddingComponent(Padding.SPACE.size)
        );

        if (!isFixed && signIcon != null) {
            ret.add(IconComponent(signIcon))
                .add(XPaddingComponent(Padding.BETWEEN_SIGN_VALUE.size));
        }

        return ret.add(TextComponent(text, shift = 1));
    }

    init {
        for (slot in AttributeModifierSlot.entries) {
            Util.defaultAttribute(attributeModifiers, slot) { attribute, modifier ->
                add(slot.toSlot(), eachAttributeTooltip(attribute, modifier, listOf(
                        EntityAttributes.ATTACK_DAMAGE,
                        EntityAttributes.ATTACK_SPEED,
                        EntityAttributes.ARMOR,
                        EntityAttributes.ARMOR_TOUGHNESS,
                        EntityAttributes.KNOCKBACK_RESISTANCE
                    )));
            }

            Util.enchantmentAttribute(itemStack, slot) { attribute, modifier ->
                add(slot.toSlot(), eachAttributeTooltip(attribute, modifier, listOf()));
            }
        }

        Util.potionAttribute(itemStack) { attribute, modifier ->
            add(Slot.DRANK, eachAttributeTooltip(attribute, modifier, listOf()));
        }

        if (lines.size > 1) {
            lines.forEach { (slot, line) ->
                line.addAll(0, LineComponent(
                    IconComponent(slot.toIcon()),
                    XPaddingComponent(Padding.SPACE.size),
                    TextComponent(">"),
                    XPaddingComponent(Padding.SLOT_MARGIN.size),
                ))
            };
        }
    }

    override fun getComponents(): List<GlanceTooltipComponent> {
        return lines.values.toList();
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