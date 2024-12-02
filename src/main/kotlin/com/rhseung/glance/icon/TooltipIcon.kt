package com.rhseung.glance.icon

import com.rhseung.glance.ModMain
import com.rhseung.glance.draw.Icon
import net.minecraft.util.Identifier

class TooltipIcon(name: String, variants: Int = 1) : Icon(variants, 9, 9) {
    override val id: Identifier = ModMain.of("textures/icon/$name.png");

    companion object {
        val HUNGER = TooltipIcon("hunger", 2);
        val SATURATION = TooltipIcon("saturation", 4);
        val FUEL = TooltipIcon("fuel");
        val DURABILITY = TooltipIcon("durability");
    }
}