package com.rhseung.glance.tooltip

import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.gradient
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

object TooltipDecor {
    data class Theme(
        val topOfOutline: Color,
        val bottomOfOutline: Color,
        val separator: Color,
        val topOfBackground: Color,
        val bottomOfBackground: Color,
        val title: Color? = null,
        val cosmetic: Identifier? = null,
    ) {
        constructor(baseColor: Color, title: Color? = null, cosmetic: Identifier? = null):
            this(
                baseColor.darker(0.1f),
                baseColor.darker(0.4f),
                baseColor.darker(0.2f),
                baseColor.darker(0.85f),
                Color.BLACK,
                title ?: baseColor,
                cosmetic
            );

        constructor(outlineColor1: Color, outlineColor2: Color, title: Color? = null, cosmetic: Identifier? = null):
            this(
                outlineColor1,
                outlineColor2,
                (outlineColor1 to outlineColor2).gradient(0.7f),
                outlineColor1.darker(0.5f),
                outlineColor2.darker(0.5f),
                title ?: outlineColor1,
                cosmetic
            );
    };

    object Themes {
        private fun id(path: String) = ModMain.id("tooltip/cosmetic/$path");

        val DEFAULT = Theme(Color(0x686868), Color(0x353535), Color.WHITE.darker(0.1f));
        val UNCOMMON = Theme(Color.YELLOW);
        val RARE = Theme(Color.AQUA);
        val EPIC = Theme(Color.LIGHT_PURPLE);

        val MUSIC = Theme(Color.WHITE, Color.WHITE, cosmetic = id("music"));
        val ENCHANT = Theme(Color(0x8B4513), Color(0x723510), Color(0xEB9A5E), id("enchant"));
        val MAP = Theme(Color(0xD6BE96));

//        val ENDER = Theme(Color(0xCA87FF), Color(0x6C11B2), id("ender"), Color(0x9f74c3));
//        val ECHO = Theme(Color(0x00FFD1), Color(0x14BDB2), id("echo")).reverseBackgroundColor();
//        val COPPER = Theme(Color(0x70453D), Color(0x542323), Color(0xf9ae9c), id("copper"));
//        val GOLD = Theme(Color(0x996922), Color(0x5B3B1D), Color(0xffbc0e), id("gold"));
//        val IRON = Theme(Color(0x7D8A91), id("iron"), Color(0xd0d7df));
//        val NETHERITE = Theme(Color(0x766A76), Color(0x5D565D), id("netherite"), Color(0x766A76));
    }

    fun themeFromItem(stack: ItemStack): Theme {
        val id = Registries.ITEM.getId(stack.item);

        return if (stack.contains(DataComponentTypes.STORED_ENCHANTMENTS))
            Themes.ENCHANT;
        else if (stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE))
            Themes.MUSIC;
        else if (stack.contains(DataComponentTypes.MAP_ID))
            Themes.MAP;
        else when (stack.rarity) {
            Rarity.COMMON -> Themes.DEFAULT;
            Rarity.UNCOMMON -> Themes.UNCOMMON;
            Rarity.RARE -> Themes.RARE;
            Rarity.EPIC -> Themes.EPIC;
        }
    }
}