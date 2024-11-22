package com.rhseung.glance.util

import net.minecraft.component.type.AttributeModifierSlot

enum class Slot {
    ANY,
    MAINHAND,
    OFFHAND,
    HAND,
    FEET,
    LEGS,
    CHEST,
    HEAD,
    ARMOR,
    BODY,
    DRANK;

    companion object {
        fun AttributeModifierSlot.toSlot() = when (this) {
            AttributeModifierSlot.ANY -> ANY
            AttributeModifierSlot.HAND -> HAND
            AttributeModifierSlot.MAINHAND -> MAINHAND
            AttributeModifierSlot.OFFHAND -> OFFHAND
            AttributeModifierSlot.FEET -> FEET
            AttributeModifierSlot.LEGS -> LEGS
            AttributeModifierSlot.CHEST -> CHEST
            AttributeModifierSlot.HEAD -> HEAD
            AttributeModifierSlot.ARMOR -> ARMOR
            AttributeModifierSlot.BODY -> BODY
        }
    }
}