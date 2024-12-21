package com.rhseung.glance.util.icon

import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Icon
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class SignIcon(private val path: String, index: Int = 0) : Icon(1, 5, 9, index) {
    override val id: Identifier = ModMain.id("textures/icon/sign/$path.png");

    override fun get(index: Int): Icon {
        if (index < 0 || index >= variants)
            throw Error("index=$index is not valid");

        return SignIcon(path, index);
    }

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