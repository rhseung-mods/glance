package com.rhseung.glance.util

import net.minecraft.item.ToolMaterial
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import kotlin.math.roundToInt

class Color {
    val R: Int
    val r: Float
        get() = R.toFloat() / 255.0f;

    val G: Int
    val g: Float
        get() = G.toFloat() / 255.0f;

    val B: Int
    val b: Float
        get() = B.toFloat() / 255.0f;

    val H: Int
    val S: Float
    val V: Float

    constructor(R: Int, G: Int, B: Int) {
        this.R = R.coerceIn(0, 255)
        this.G = G.coerceIn(0, 255)
        this.B = B.coerceIn(0, 255)
        val r = this.R / 255.0F
        val g = this.G / 255.0F
        val b = this.B / 255.0F

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)

        this.H = ((when (max) {
            min -> 0.0F
            r -> (g - b) / (max - min)
            g -> 2 + (b - r) / (max - min)
            b -> 4 + (r - g) / (max - min)
            else -> 0.0F
        } * 60).roundToInt() + 360) % 360

        this.S = when (max) {
            0.0F -> 0.0F
            else -> (max - min) / max
        }

        this.V = max
    }

    constructor(H: Int, S: Float, V: Float) {
        this.H = H.coerceIn(0, 360)
        this.S = S.coerceIn(0.0F, 1.0F)
        this.V = V.coerceIn(0.0F, 1.0F)
        val max = (this.V * 255).roundToInt()
        val min = (max * (1 - this.S)).roundToInt()

        when (this.H) {
            in 300 until 360 -> {
                this.R = max
                this.G = min
                this.B = (-((this.H - 360) / 60.0) * (max - min) + this.G).roundToInt()
            }

            in 0 until 60 -> {
                this.R = max
                this.B = min
                this.G = ((this.H / 60.0) * (max - min) + this.B).roundToInt()
            }

            in 60 until 120 -> {
                this.G = max
                this.B = min
                this.R = (-(this.H / 60.0 - 2) * (max - min) + this.B).roundToInt()
            }

            in 120 until 180 -> {
                this.G = max
                this.R = min
                this.B = ((this.H / 60.0 - 2) * (max - min) + this.R).roundToInt()
            }

            in 180 until 240 -> {
                this.B = max
                this.R = min
                this.G = (-(this.H / 60.0 - 4) * (max - min) + this.R).roundToInt()
            }

            in 240 until 300 -> {
                this.B = max
                this.G = min
                this.R = ((this.H / 60.0 - 4) * (max - min) + this.G).roundToInt()
            }

            else -> error("impossible")
        }
    }

    constructor(rgb: Int) : this((rgb shr 16) and 0xFF, (rgb shr 8) and 0xFF, rgb and 0xFF);

    override fun toString(): String {
        return Integer.toHexString(1 shl 24 or (R shl 16) or (G shl 8) or B).substring(1);
    }

    fun toInt(shift: Boolean = false): Int {
        return if (!shift) Integer.parseInt(toString(), 16)
        else Integer.parseInt(toString(), 16) - (2 shl 23);
    }

    fun toTextColor(): TextColor {
        return TextColor.fromRgb(toInt());
    }

    fun darker(delta: Int): Color {
        return Color(
            (R - delta).coerceIn(0, 255),
            (G - delta).coerceIn(0, 255),
            (B - delta).coerceIn(0, 255)
        );
    }

    fun darker(delta: Float): Color {
        return darker((delta * 255).toInt());
    }

    fun brighter(delta: Int): Color {
        return Color(
            (R + delta).coerceIn(0, 255),
            (G + delta).coerceIn(0, 255),
            (B + delta).coerceIn(0, 255)
        );
    }

    fun brighter(delta: Float): Color {
        return brighter((delta * 255).toInt());
    }

    companion object {
        val WOOD = Color(150, 116, 65);
        val STONE = Color(149, 145, 141);
        val IRON = Color(215, 215, 215);
        val DIAMOND = Color(110, 236, 210);
        val NETHERITE = Color(98, 88, 89);

        val BLACK = Color(0);
        val DARK_BLUE = Color(170);
        val DARK_GREEN = Color(43520);
        val DARK_AQUA = Color(43690);
        val DARK_RED = Color(11141120);
        val DARK_PURPLE = Color(11141290);
        val GOLD = Color(16755200);
        val GRAY = Color(11184810);
        val DARK_GRAY = Color(5592405);
        val BLUE = Color(5592575);
        val GREEN = Color(5635925);
        val AQUA = Color(5636095);
        val RED = Color(16733525);
        val LIGHT_PURPLE = Color(16733695);
        val YELLOW = Color(16777045);
        val WHITE = Color(16777215);

        val FUEL = Color(0xE9B83B);

        fun Pair<Color, Color>.gradient(ratioOfFirst: Float): Color {
            return Color(
                (this.first.H * ratioOfFirst + this.second.H * (1 - ratioOfFirst)).roundToInt(),
                this.first.S * ratioOfFirst + this.second.S * (1 - ratioOfFirst),
                this.first.V * ratioOfFirst + this.second.V * (1 - ratioOfFirst)
            );
        }

        fun ToolMaterial.toColor() = when (this) {
            ToolMaterial.WOOD -> WOOD
            ToolMaterial.GOLD -> GOLD
            ToolMaterial.STONE -> STONE
            ToolMaterial.IRON -> IRON
            ToolMaterial.DIAMOND -> DIAMOND
            ToolMaterial.NETHERITE -> NETHERITE
            else -> error("Unknown tool material")
        }

        fun Formatting.toColor() = when (this) {
            Formatting.BLACK -> BLACK
            Formatting.DARK_BLUE -> DARK_BLUE
            Formatting.DARK_GREEN -> DARK_GREEN
            Formatting.DARK_AQUA -> DARK_AQUA
            Formatting.DARK_RED -> DARK_RED
            Formatting.DARK_PURPLE -> DARK_PURPLE
            Formatting.GOLD -> GOLD
            Formatting.GRAY -> GRAY
            Formatting.DARK_GRAY -> DARK_GRAY
            Formatting.BLUE -> BLUE
            Formatting.GREEN -> GREEN
            Formatting.AQUA -> AQUA
            Formatting.RED -> RED
            Formatting.LIGHT_PURPLE -> LIGHT_PURPLE
            Formatting.YELLOW -> YELLOW
            Formatting.WHITE -> WHITE
            else -> error("Formatting Modifier cannot be converted to Color")
        }
    }
}
