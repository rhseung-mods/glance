package com.rhseung.glance.tooltip.util

import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Color.Companion.toColor
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

enum class TooltipSeparator {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC;

    val id: Identifier = ModMain.id("textures/frame/default.png");

    fun draw(context: DrawContext, xStart: Int, xEnd: Int, y: Int): Int {
        var x = xStart;

        var start = 0;
        var end = xEnd - xStart;
        var middle = (start + end) / 2;

        while (x <= xEnd) {
            var now_x = x - xStart;
            var current = if (now_x < middle) now_x else end - now_x;
            var currentRatio = current.toFloat() / middle.toFloat();    // current가 middle이 되면 1.
//            var color = (toRarity().formatting.toColor() to BACKGROUND_COLOR).gradient(currentRatio);
            var color = toRarity().formatting.toColor().darker(((1 - currentRatio) * 255).toInt());

            context.drawTexture(RenderLayer::getGuiTextured, id,
                x,
                y,
//                (if (x - xStart < TEXTURE_WIDTH) x - xStart else if (xEnd - x < TEXTURE_WIDTH) xEnd - x else TEXTURE_WIDTH - 1).toFloat(),
                2F,
                0F,
                WIDTH,
                HEIGHT,
                TEXTURE_WIDTH,
                HEIGHT,
                color.toInt() - (2 shl 23)
            );
            x += WIDTH;
        }

        return x;
    }

    val WIDTH = 1;
    val HEIGHT = 1;
    val TEXTURE_WIDTH = 3 * WIDTH;

    fun toRarity(): Rarity {
        return when (this) {
            COMMON -> Rarity.COMMON;
            UNCOMMON -> Rarity.UNCOMMON;
            RARE -> Rarity.RARE;
            EPIC -> Rarity.EPIC;
        }
    }
}