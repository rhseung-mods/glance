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
        val cosmetic: Identifier? = null,
        val title: Color? = null,
    ) {
        constructor(color: Color, cosmetic: Identifier? = null, title: Color? = null):
            this(color, color.darker(0.2f), color.darker(0.1f), color.darker(0.85f), Color.BLACK, cosmetic, title ?: color);

        constructor(outlineColor1: Color, outlineColor2: Color, cosmetic: Identifier? = null, title: Color? = null):
            this(outlineColor1, outlineColor2, (outlineColor1 to outlineColor2).gradient(0.5f), outlineColor1.darker(0.5f), Color.BLACK, cosmetic, title ?: outlineColor1);

        fun reverseBackgroundColor(): Theme {
            return Theme(topOfOutline, bottomOfOutline, separator,
                bottomOfBackground, topOfBackground, cosmetic, title);
        }
    };

    object Themes {
        private fun id(path: String) = ModMain.id("tooltip/cosmetic/$path");

        val DEFAULT = Theme(Color(0x686868), Color(0x353535), null, Color.WHITE.darker(0.1f));
        val UNCOMMON = Theme(Color.YELLOW.darker(0.2f), Color.YELLOW.darker(0.5f));
        val RARE = Theme(Color.AQUA.darker(0.2f), Color.AQUA.darker(0.5f));
        val EPIC = Theme(Color.LIGHT_PURPLE.darker(0.2f), Color.LIGHT_PURPLE.darker(0.5f));

        val MUSIC = Theme(Color.WHITE, id("music"), Color.WHITE);
        val ENDER = Theme(Color(0xCA87FF), Color(0x6C11B2), id("ender"), Color(0x9f74c3));
        val ENCHANT = Theme(Color(0x8B4513), Color(0x723510), id("enchant"), Color(0xEB9A5E));
        val ECHO = Theme(Color(0x00FFD1), Color(0x14BDB2), id("echo")).reverseBackgroundColor();

        val COPPER = Theme(Color(0x70453D), Color(0x542323), id("copper"), Color(0xf9ae9c));
        val GOLD = Theme(Color(0x996922), Color(0x5B3B1D), id("gold"), Color(0xffbc0e));
        val SILVER = Theme(Color(0x697C8C), Color(0x3A4C61), id("silver"), Color(0xDDEFFF));
        val IRON = Theme(Color(0x7D8A91), id("iron"), Color(0xd0d7df));
        val NETHERITE = Theme(Color(0x766A76), Color(0x5D565D), id("netherite"), Color(0x766A76));
    }

    fun themeFromItem(stack: ItemStack): Theme {
        val id = Registries.ITEM.getId(stack.item);

        return if (stack.contains(DataComponentTypes.STORED_ENCHANTMENTS))
            Themes.ENCHANT;
        else if (stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE))
            Themes.MUSIC;
        else if ("copper" in id.path)
            Themes.COPPER;
        else if ("gold" in id.path)
            Themes.GOLD;
        else if ("iron" in id.path)
            Themes.IRON;
        else if ("netherite" in id.path)
            Themes.NETHERITE;
        else if ("ender" in id.path)
            Themes.ENDER;
        else if ("sculk" in id.path || "echo" in id.path)
            Themes.ECHO;
        else when (stack.rarity) {
            Rarity.COMMON -> Themes.DEFAULT;
            Rarity.UNCOMMON -> Themes.UNCOMMON;
            Rarity.RARE -> Themes.RARE;
            Rarity.EPIC -> Themes.EPIC;
        }
    }
}