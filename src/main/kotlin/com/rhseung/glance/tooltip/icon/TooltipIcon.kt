package com.rhseung.glance.tooltip.icon

import com.rhseung.glance.ModMain
import net.minecraft.util.Identifier

class TooltipIcon(val name: String, variants: Int = 1, index: Int = 0) : Icon(variants, 9, 9, index) {
    override val id: Identifier = ModMain.id("textures/icon/$name.png");

    override fun get(index: Int): TooltipIcon {
        if (index < 0 || index >= variants)
            throw Error("index=$index is not valid");

        return TooltipIcon(name, variants, index);
    }

    companion object {
        val FOOD = TooltipIcon("food", 2);
        val FOOD_HUNGER = TooltipIcon("food_hunger", 2);
        val SATURATION = TooltipIcon("saturation", 4);
        val SATURATION_HUNGER = TooltipIcon("saturation_hunger", 4);
        val FUEL = TooltipIcon("fuel");
        val DURABILITY = TooltipIcon("durability");
    }
}