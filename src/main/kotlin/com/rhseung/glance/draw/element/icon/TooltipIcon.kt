package com.rhseung.glance.draw.element.icon

import com.rhseung.glance.ModMain
import com.rhseung.glance.draw.element.Icon
import net.minecraft.util.Identifier

class TooltipIcon(val name: String, variants: Int = 1, index: Int = 0) : Icon(variants, 9, 9, index) {
    override val id: Identifier = ModMain.id("textures/icon/$name.png");

    override fun get(index: Int): Icon {
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