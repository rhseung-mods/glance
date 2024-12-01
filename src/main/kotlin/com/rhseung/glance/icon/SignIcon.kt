package com.rhseung.glance.icon

import com.rhseung.glance.ModMain
import com.rhseung.glance.icon.Icon.Companion
import com.rhseung.glance.util.Slot
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class SignIcon(private val path: String) : Icon(1) {
    override val id: Identifier = ModMain.of("textures/icon/sign/$path.png");

    override fun draw(context: DrawContext, x: Int, y: Int, index: Int): Int {
        context.drawTexture(
            RenderLayer::getGuiTextured, id,
            x,
            y - 1,
            WIDTH.toFloat() * index,
            0F,
            WIDTH,
            HEIGHT,
            WIDTH * variants,
            HEIGHT,
        );

        return x + WIDTH;
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

        val WIDTH = 5;
        val HEIGHT = 9;
    }
}