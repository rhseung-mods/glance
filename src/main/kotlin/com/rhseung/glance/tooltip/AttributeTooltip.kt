package com.rhseung.glance.tooltip

import com.rhseung.glance.icon.Icon
import com.rhseung.glance.util.IconText
import com.rhseung.glance.util.IconText.Companion.draw
import com.rhseung.glance.util.IconText.Companion.getWidth
import com.rhseung.glance.icon.SlotIcon.Companion.toIcon
import com.rhseung.glance.icon.TooltipIcon
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Draw
import com.rhseung.glance.util.Draw.draw
import com.rhseung.glance.util.Draw.toText
import com.rhseung.glance.util.Slot
import com.rhseung.glance.util.Slot.Companion.toSlot
import com.rhseung.glance.util.Util
import com.rhseung.glance.util.Util.roundTo
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

class AttributeTooltip(override val data: AttributeTooltipData) : AbstractTooltip(data) {
    class AttributeTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) :
        AbstractTooltipData(item, stack, client) {
        private val attributeModifiersComponent = item.components.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        private val attributeModifiers = attributeModifiersComponent.modifiers;
        private val player = client.player;

        val attributes: MutableMap<Slot, MutableList<IconText>> = mutableMapOf();

        val durability = stack.maxDamage - stack.damage;
        val maxDurability = stack.maxDamage;
        val durabilityText = listOf(
            "$durability" to Color.WHITE,
            "/$maxDurability" to Color.DARK_GRAY
        ).toText();

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

                return if (value == 0.0)
                    null;
                else
                    IconText(attribute, value, bl, isMultiplier);
//                else if (bl)
//                    IconText(attribute, value, true);
//                else if (value > 0)
//                    IconText(attribute, value, false);
//                else
//                    IconText(attribute, value, false);
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
        val height = Icon.HEIGHT * size + Draw.LINE_MARGIN * size;

        return if (size > 1 && data.client.options.advancedItemTooltips) height + Icon.HEIGHT + Draw.LINE_MARGIN else height;
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        if (Screen.hasShiftDown()) return 0;
        if (data.attributes.isEmpty()) return 0;

        // slotWidth가 maxWidth + durability보다 작을 수도 있긴 해
        val maxWidth = data.attributes.maxOf { it.value.getWidth(textRenderer) };
        val slotWidth = maxWidth + if (data.attributes.size == 1) 0 else Icon.WIDTH + Draw.SPACE + textRenderer.getWidth(">") + Draw.SLOT_MARGIN;

        return slotWidth + if (data.attributes.size == 1 && data.client.options.advancedItemTooltips) Draw.NEXT_ICON_MARGIN + Icon.WIDTH + Draw.SPACE + textRenderer.getWidth(data.durabilityText) else 0;
    }

    private fun drawDurability(x0: Int, y0: Int, context: DrawContext, textRenderer: TextRenderer): Int {
        var x = x0;
        x = TooltipIcon.DURABILITY.draw(context, x, y0) + Draw.SPACE;
        x = data.durabilityText.draw(context, textRenderer, x, y0) + Draw.NEXT_ICON_MARGIN;

        return x;
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

        var x = x0 + Draw.ICON_START_PADDING;
        var y = y0;
        val showSlot = data.attributes.size > 1;

        data.attributes.forEach { (slot, iconTexts) ->
            if (showSlot) {
                x = slot.toIcon().draw(context, x, y) + Draw.SPACE;
                x = ">".draw(context, textRenderer, x, y, Color.WHITE.darker(100)) + Draw.SLOT_MARGIN;
            }

            x = iconTexts.draw(context, textRenderer, x, y);

            if (!showSlot && data.client.options.advancedItemTooltips) {
                x = drawDurability(x, y0, context, textRenderer);
            }

            x = x0 + Draw.ICON_START_PADDING;
            y += Icon.HEIGHT + Draw.LINE_MARGIN;
        }

        if (showSlot && data.client.options.advancedItemTooltips) {
            x = drawDurability(x, y0, context, textRenderer);
        }
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<AttributeTooltipData>(::AttributeTooltip);
            TooltipDataFactoryManager.set<Item>(::AttributeTooltipData);
        }
    }
}