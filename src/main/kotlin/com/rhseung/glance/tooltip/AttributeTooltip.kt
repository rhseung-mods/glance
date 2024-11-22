package com.rhseung.glance.tooltip

import com.rhseung.glance.icon.AttributeIcon.Companion.toIcon
import com.rhseung.glance.icon.Icon
import com.rhseung.glance.icon.IconText
import com.rhseung.glance.icon.IconText.Companion.draw
import com.rhseung.glance.icon.IconText.Companion.getWidth
import com.rhseung.glance.icon.SlotIcon.Companion.toIcon
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.toColor
import com.rhseung.glance.tooltip.Draw.draw
import com.rhseung.glance.util.Slot
import com.rhseung.glance.util.Slot.Companion.toSlot
import com.rhseung.glance.util.Util
import com.rhseung.glance.util.Util.roundTo
import com.rhseung.glance.util.Util.toStringPretty
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.entry.RegistryEntry
import kotlin.math.abs

class AttributeTooltip(override val data: AttributeTooltipData) : AbstractTooltip(data) {
    class AttributeTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) :
        AbstractTooltipData(item, stack, client) {
        private val attributeModifiersComponent = item.components.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        private val attributeModifiers = attributeModifiersComponent.modifiers;
        private val player = client.player;

        val attributes: MutableMap<Slot, MutableList<IconText>> = mutableMapOf();
        private val blTrueAttributes: List<RegistryEntry<EntityAttribute>> = listOf(
            EntityAttributes.ATTACK_DAMAGE,
            EntityAttributes.ATTACK_SPEED,
            EntityAttributes.ARMOR,
            EntityAttributes.ARMOR_TOUGHNESS,
            EntityAttributes.KNOCKBACK_RESISTANCE
        );

        private fun add(slot: Slot, iconText: IconText?) {
            if (iconText == null) return;

            if (slot !in attributes)
                attributes[slot] = mutableListOf();

            attributes[slot]!!.add(iconText);
        }

        companion object {
            fun equals(attr1: RegistryEntry<EntityAttribute>, attr2: RegistryEntry<EntityAttribute>): Boolean {
                return attr1.matches(attr2);
            }

            /**
             * [net.minecraft.item.ItemStack.appendAttributeModifierTooltip]
             */
            fun getIconText(
                player: PlayerEntity?,
                attribute: RegistryEntry<EntityAttribute>,
                modifier: EntityAttributeModifier,
                blTrueAttributes: List<RegistryEntry<EntityAttribute>>
            ): IconText? {
                var value = modifier.value;

                var bl = false;
                if (player != null && blTrueAttributes.find { equals(it, attribute) } != null) {
                    value += player.getAttributeBaseValue(attribute);
                    bl = true;
                }

                value = value.roundTo(2);

                var isMultiplier = false;
                if (modifier.operation != Operation.ADD_MULTIPLIED_BASE && modifier.operation != Operation.ADD_MULTIPLIED_TOTAL) {
                    if (equals(attribute, EntityAttributes.KNOCKBACK_RESISTANCE))
                        value *= 10;
                } else {
                    isMultiplier = true;
                }

                var text = abs(value).toStringPretty();
                if (isMultiplier)
                    text += "x";
                if (!bl) {
                    if (value > 0)
                        text = "+$text";
                    else if (value < 0)
                        text = "-$text";
                }

                return if (value == 0.0)
                    null;
                else if (bl)
                    IconText(attribute.toIcon(), text);
                else if (value > 0)
                    IconText(attribute.toIcon(), text, attribute.value().getFormatting(true).toColor());
                else
                    IconText(attribute.toIcon(), text, attribute.value().getFormatting(false).toColor());
            }
        }
        /**
         * [net.minecraft.item.ItemStack.appendAttributeModifiersTooltip]
         */
        init {
            for (slot in AttributeModifierSlot.entries) {
                Util.defaultAttribute(attributeModifiers, slot) { attribute, modifier ->
                    add(slot.toSlot(), getIconText(player, attribute, modifier, blTrueAttributes));
                }

                Util.enchantmentAttribute(stack, slot) { attribute, modifier ->
                    add(slot.toSlot(), getIconText(player, attribute, modifier, blTrueAttributes));
                }
            }

            Util.potionAttribute(stack) { attribute, modifier ->
                add(Slot.DRANK, getIconText(player, attribute, modifier, listOf()));
            }
        }
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        if (Screen.hasShiftDown()) return 0;

        val size = data.attributes.size;
        return Icon.HEIGHT * size + Draw.LINE_MARGIN * size;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        if (Screen.hasShiftDown()) return 0;
        if (data.attributes.isEmpty()) return 0;

        val width = data.attributes.maxOf { it.value.getWidth(textRenderer) };
        return if (data.attributes.size == 1) width else Icon.WIDTH + Draw.SPACE + textRenderer.getWidth(">") + Draw.SLOT_MARGIN + width;
    }

    override fun drawItems(
        textRenderer: TextRenderer,
        x0: Int,
        y0: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        if (Screen.hasShiftDown()) return;

        var x = x0;
        var y = y0;
        val showSlot = data.attributes.size > 1;

        data.attributes.forEach { (slot, iconTexts) ->
            if (showSlot) {
                x = slot.toIcon().draw(context, x, y) + Draw.SPACE;
                x = ">".draw(context, textRenderer, x, y, Color.WHITE.darker(100)) + Draw.SLOT_MARGIN;
            }

            iconTexts.draw(context, textRenderer, x, y);

            x = x0;
            y += Icon.HEIGHT + Draw.LINE_MARGIN;
        }
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<AttributeTooltipData>(::AttributeTooltip);
            TooltipDataFactoryManager.set<Item>(::AttributeTooltipData);
        }
    }
}