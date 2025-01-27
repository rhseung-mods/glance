package com.rhseung.glance.util

import net.minecraft.component.type.AttributeModifierSlot

enum class Slot(val translationKey: String) {
    ANY("item.modifiers." + AttributeModifierSlot.ANY.asString()),
    MAINHAND("item.modifiers." + AttributeModifierSlot.MAINHAND.asString()),
    OFFHAND("item.modifiers." + AttributeModifierSlot.OFFHAND.asString()),
    HAND("item.modifiers." + AttributeModifierSlot.HAND.asString()),
    FEET("item.modifiers." + AttributeModifierSlot.FEET.asString()),
    LEGS("item.modifiers." + AttributeModifierSlot.LEGS.asString()),
    CHEST("item.modifiers." + AttributeModifierSlot.CHEST.asString()),
    HEAD("item.modifiers." + AttributeModifierSlot.HEAD.asString()),
    ARMOR("item.modifiers." + AttributeModifierSlot.ARMOR.asString()),
    BODY("item.modifiers." + AttributeModifierSlot.BODY.asString()),
    DRANK("potion.whenDrank");

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