package com.rhseung.glance.util

import net.minecraft.item.ToolMaterial
import net.minecraft.text.MutableText
import net.minecraft.text.Text
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

    constructor(str: String) : this(str.substring(1).toInt(16));

    fun rgb(): Int {
        return (R shl 16) or (G shl 8) or B;
    }

    fun argb(alpha: Int): Int {
        return (alpha shl 24) or rgb();
    }

    fun zeroAlpha(): Int {
        return argb(0);
    }

    fun fullAlpha(): Int {
        return argb(255);
    }

    fun withAlpha(alpha: Int): Int {
        return argb(alpha);
    }

    fun toInt(alpha: Int = 255): Int {
        return argb(alpha);
    }

    override fun toString(): String {
        return "#%06X".format(rgb());
    }

    fun darker(delta: Float): Color {
        return Color(H, S, V - delta);
    }

    fun brighter(delta: Float): Color {
        return Color(H, S, V + delta);
    }

    override fun equals(other: Any?): Boolean {
        return other is Color && R == other.R && G == other.G && B == other.B;
    }

    override fun hashCode(): Int {
        var result = R;
        result = 31 * result + G;
        result = 31 * result + B;
        return result;
    }

    companion object {
        fun empty(): Int {
            return Color.WHITE.argb(0);    // -1와 동일
        }

        infix fun String.with(color: Color): MutableText {
            return Text.literal(this).withColor(color.toInt());
        }

        val WOOD = Color(150, 116, 65);
        val STONE = Color(149, 145, 141);
        val IRON = Color(215, 215, 215);
        val DIAMOND = Color(110, 236, 210);
        val NETHERITE = Color(98, 88, 89);

        val BLACK = Color(0x000000);
        val DARK_BLUE = Color(0x0000AA);
        val DARK_GREEN = Color(0x00AA00);
        val DARK_AQUA = Color(0x00AAAA);
        val DARK_RED = Color(0xAA0000);
        val DARK_PURPLE = Color(0xAA00AA);
        val GOLD = Color(0xFFAA00);
        val GRAY = Color(0xAAAAAA);
        val DARK_GRAY = Color(0x555555);
        val BLUE = Color(0x5555FF);
        val GREEN = Color(0x55FF55);
        val AQUA = Color(0x55FFFF);
        val RED = Color(0xFF5555);
        val LIGHT_PURPLE = Color(0xFF55FF);
        val YELLOW = Color(0xFFFF55);
        val WHITE = Color(0xFFFFFF);

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
            else -> error("Formatting($this) cannot be converted to color")
        }
    }
}
