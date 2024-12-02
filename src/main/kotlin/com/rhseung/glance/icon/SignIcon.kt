package com.rhseung.glance.icon

import com.rhseung.glance.ModMain
import com.rhseung.glance.draw.Icon
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class SignIcon(private val path: String) : Icon(1, 5, 9) {
    override val id: Identifier = ModMain.of("textures/icon/sign/$path.png");

    companion object {
        val POSITIVE_UP = SignIcon("positive_up");
        val POSITIVE_DOWN = SignIcon("positive_down");
        val NEGATIVE_UP = SignIcon("negative_up");
        val NEGATIVE_DOWN = SignIcon("negative_down");

        fun EntityAttribute.toSignIcon(value: Double): SignIcon? {
            val formatting = this.getFormatting(value > 0);

            return when {
                formatting == Formatting.BLUE && value > 0 -> POSITIVE_UP;
                formatting == Formatting.BLUE && value < 0 -> POSITIVE_DOWN;
                formatting == Formatting.RED && value > 0 -> NEGATIVE_UP;
                formatting == Formatting.RED && value < 0 -> NEGATIVE_DOWN;
                else -> null;
            }
        }
    }
}