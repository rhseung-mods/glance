package com.rhseung.glance.tooltip

import com.rhseung.glance.draw.DrawableLine
import com.rhseung.glance.draw.DrawableTooltip
import com.rhseung.glance.draw.element.GlanceText.Companion.toGlanceText
import com.rhseung.glance.draw.element.Padding
import com.rhseung.glance.draw.element.StackDisplay
import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class EnchantedBookTooltip(data: EnchantmentTooltipData) :
    AbstractTooltip<EnchantedBookTooltip.EnchantmentTooltipData>(data) {
    class EnchantmentTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) : AbstractTooltipData(item, stack, client) {
        private val enchantmentComponent = EnchantmentHelper.getEnchantments(stack);
        private val enchantmentEntries = enchantmentComponent.enchantmentEntries;
        private val testItems = listOf(
            "minecraft:diamond_sword", "minecraft:diamond_pickaxe", "minecraft:diamond_axe", "minecraft:diamond_shovel", "minecraft:diamond_hoe",
            "minecraft:diamond_helmet", "minecraft:diamond_chestplate", "minecraft:diamond_leggings", "minecraft:diamond_boots",
            "minecraft:shears", "minecraft:bow", "minecraft:crossbow", "minecraft:trident", "minecraft:mace", "minecraft:fishing_rod", "minecraft:elytra", "minecraft:shield",
            "quark:pickarang", "supplementaries:slingshot", "supplementaries:bubble_blower", "farmersdelight:diamond_knife", "the_bumblezone:stinger_spear",
            "the_bumblezone:crystal_cannon", "the_bumblezone:honey_crystal_shield", "the_bumblezone:honey_bee_leggings_2"
        ).map(Identifier::of).map(Registries.ITEM::get).filter { it != Items.AIR }.map(::ItemStack);

        override fun getTooltip(): DrawableTooltip {
            var tooltip = DrawableTooltip();

            enchantmentEntries.forEach { (enchantmentEntry, level) ->
                val enchantment = enchantmentEntry.value();
                tooltip += DrawableLine(Enchantment.getName(enchantmentEntry, level).toGlanceText());

                var line = Padding.ICON_START + Padding.ENCHANT_TEST_ITEM_MARGIN;
                testItems.forEach { testItem ->
                    if (enchantment.isAcceptableItem(testItem)) {
                        val stackDisplay = StackDisplay(testItem, 9);
                        line = line + (Padding.SPACE * stackDisplay.ratio) + stackDisplay;
                    }
                }

                tooltip += line;
            }

            return tooltip;
        }
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<EnchantmentTooltipData>(::EnchantedBookTooltip);
            TooltipDataFactoryManager.set<Item> { item, stack, client ->
                if (DataComponentTypes.STORED_ENCHANTMENTS in item.components)
                    EnchantmentTooltipData(item, stack, client);
                else
                    null;
            };
        }
    }
}