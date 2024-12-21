package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.component.GlanceTooltipComponent
import com.rhseung.glance.tooltip.component.ItemStackComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.tooltip.component.XPaddingComponent
import net.minecraft.component.DataComponentTypes
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class EnchantmentContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    private val enchantmentComponent = EnchantmentHelper.getEnchantments(itemStack);
    private val enchantmentEntries = enchantmentComponent.enchantmentEntries;
    private val testItems = listOf(
        "minecraft:diamond_sword", "minecraft:diamond_pickaxe", "minecraft:diamond_axe", "minecraft:diamond_shovel", "minecraft:diamond_hoe",
        "minecraft:diamond_helmet", "minecraft:diamond_chestplate", "minecraft:diamond_leggings", "minecraft:diamond_boots",
        "minecraft:shears", "minecraft:bow", "minecraft:crossbow", "minecraft:trident", "minecraft:mace", "minecraft:fishing_rod", "minecraft:elytra", "minecraft:shield",
        "quark:pickarang", "supplementaries:slingshot", "supplementaries:bubble_blower", "farmersdelight:diamond_knife", "the_bumblezone:stinger_spear",
        "the_bumblezone:crystal_cannon", "the_bumblezone:honey_crystal_shield", "the_bumblezone:honey_bee_leggings_2"
    ).map(Identifier::of).map(Registries.ITEM::get).filter { it != Items.AIR }.map(::ItemStack);

    override fun getComponents(): List<GlanceTooltipComponent> {
        val ret = mutableListOf<GlanceTooltipComponent>();

        enchantmentEntries.forEach { (entry, level) ->
            val enchantment = entry.value();
            ret.add(TextComponent(Enchantment.getName(entry, level)));

            val line = LineComponent(XPaddingComponent(9));
            testItems.forEach { testItem ->
                if (enchantment.isAcceptableItem(testItem)) {
                    val stackComponent = ItemStackComponent(testItem, 9);
                    line.add(XPaddingComponent((3 * stackComponent.ratio).toInt())).add(stackComponent);
                }
            }
            ret.add(line);
        }

        return ret;
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::EnchantmentContent, EnchantmentContent::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return item.components.contains(DataComponentTypes.STORED_ENCHANTMENTS);
        }
    }
}