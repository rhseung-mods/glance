package com.rhseung.glance.icon

import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Slot
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.util.Identifier

class SlotIcon(private val slot: Slot) : Icon(1) {
    override val id: Identifier = ModMain.of("textures/icon/slot/${slot.name.lowercase()}.png");

    fun toSlot() = slot;

    companion object {
        val ANY = SlotIcon(Slot.ANY);
        val MAINHAND = SlotIcon(Slot.MAINHAND);
        val OFFHAND = SlotIcon(Slot.OFFHAND);
        val HAND = SlotIcon(Slot.HAND);
        val FEET = SlotIcon(Slot.FEET);
        val LEGS = SlotIcon(Slot.LEGS);
        val CHEST = SlotIcon(Slot.CHEST);
        val HEAD = SlotIcon(Slot.HEAD);
        val ARMOR = SlotIcon(Slot.ARMOR);
        val BODY = SlotIcon(Slot.BODY);
        val DRANK = SlotIcon(Slot.DRANK);  // translation='potion.whenDrank'

        fun AttributeModifierSlot.toIcon() = when (this) {
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

        fun Slot.toIcon() = when (this) {
            Slot.ANY -> ANY
            Slot.MAINHAND -> MAINHAND
            Slot.OFFHAND -> OFFHAND
            Slot.HAND -> HAND
            Slot.FEET -> FEET
            Slot.LEGS -> LEGS
            Slot.CHEST -> CHEST
            Slot.HEAD -> HEAD
            Slot.ARMOR -> ARMOR
            Slot.BODY -> BODY
            Slot.DRANK -> DRANK
        }
    }
}