package com.rhseung.glance.tooltip

import com.rhseung.glance.draw.DrawableLine
import com.rhseung.glance.draw.DrawableTooltip
import com.rhseung.glance.draw.element.GlanceText
import com.rhseung.glance.draw.element.Padding
import com.rhseung.glance.draw.element.GlanceText.Companion.with
import com.rhseung.glance.draw.element.icon.AttributeIcon.Companion.toIcon
import com.rhseung.glance.draw.element.icon.SignIcon
import com.rhseung.glance.draw.element.icon.SignIcon.Companion.toSignIcon
import com.rhseung.glance.draw.element.icon.SlotIcon.Companion.toIcon
import com.rhseung.glance.draw.element.icon.TooltipIcon
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.tooltip.util.SpecialChar
import com.rhseung.glance.util.*
import com.rhseung.glance.util.Color.Companion.toColor
import com.rhseung.glance.util.Slot.Companion.toSlot
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

class AttributeTooltip(data: AttributeTooltipData) : AbstractTooltip<AttributeTooltip.AttributeTooltipData>(data) {
    class AttributeTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) :
        AbstractTooltipData(item, stack, client) {
        private val attributeModifiersComponent = item.components.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        private val attributeModifiers = attributeModifiersComponent.modifiers;
        private val player = client.player;

        private val durability = stack.maxDamage - stack.damage;
        private val maxDurability = stack.maxDamage;

        private val durabilityTooltip = TooltipIcon.DURABILITY +
            ("$durability" with Color.WHITE) +
            ("/$maxDurability" with Color.DARK_GRAY);

        private val attributes: MutableMap<Slot, DrawableLine> = mutableMapOf();

        private fun add(slot: Slot, attributeTooltip: DrawableLine?) {
            if (attributeTooltip == null) return;

            if (slot !in attributes)
                attributes[slot] = attributeTooltip;
            else
                attributes[slot] = attributes[slot]!! + Padding.NEXT_ICON + attributeTooltip;
        }

        /**
         * [net.minecraft.item.ItemStack.appendAttributeModifierTooltip]
         */
        private fun attributeTooltip(
            attribute: RegistryEntry<EntityAttribute>,
            modifier: EntityAttributeModifier,
            blTrueAttributes: List<RegistryEntry<EntityAttribute>>
        ): DrawableLine? {
            val player = client.player;

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
            val text: GlanceText = (content + if (isMultiplier) SpecialChar.MULTIPLY else "") with color;

            return attribute.toIcon() + Padding.SPACE + if (!isFixed && signIcon != null)
                signIcon + Padding.BETWEEN_SIGN_VALUE + text
            else
                DrawableLine(text);
        }

        /**
         * [net.minecraft.item.ItemStack.appendAttributeModifiersTooltip]
         */
        init {
            for (slot in AttributeModifierSlot.entries) {
                Util.defaultAttribute(attributeModifiers, slot) { attribute, modifier ->
                    add(slot.toSlot(), attributeTooltip(attribute, modifier, listOf(
                        EntityAttributes.ATTACK_DAMAGE,
                        EntityAttributes.ATTACK_SPEED,
                        EntityAttributes.ARMOR,
                        EntityAttributes.ARMOR_TOUGHNESS,
                        EntityAttributes.KNOCKBACK_RESISTANCE
                    )));
                }

                Util.enchantmentAttribute(stack, slot) { attribute, modifier ->
                    add(slot.toSlot(), attributeTooltip(attribute, modifier, listOf()));
                }
            }

            Util.potionAttribute(stack) { attribute, modifier ->
                add(Slot.DRANK, attributeTooltip(attribute, modifier, listOf()));
            }

            if (client.options.advancedItemTooltips && attributes.size == 1) {
                attributes[attributes.keys.first()] =
                    attributes[attributes.keys.first()]!! + (Padding.NEXT_ICON + durabilityTooltip);
            }
        }

        override fun getTooltip(): DrawableTooltip {
            var ret = DrawableTooltip();
            if (attributes.size > 1)
                attributes.forEach { (slot, line) -> ret += slot.toIcon() + Padding.SPACE + ">" + Padding.SLOT_MARGIN + line; };
            else
                attributes.forEach { (_, line) -> ret += line; };

            if (client.options.advancedItemTooltips && attributes.size > 1)
                ret += durabilityTooltip;

            return ret;
        }
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<AttributeTooltipData>(::AttributeTooltip);
            TooltipDataFactoryManager.set<Item> { item, stack, client ->
                if (!Screen.hasShiftDown())
                    AttributeTooltipData(item, stack, client)
                else
                    null
            };
        }
    }
}