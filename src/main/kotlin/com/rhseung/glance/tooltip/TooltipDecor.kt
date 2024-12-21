package com.rhseung.glance.tooltip

import com.mojang.blaze3d.systems.RenderSystem
import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.gradient
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

object TooltipDecor {
    data class Theme(
        val outlineColor1: Color,
        val outlineColor2: Color,
        val sepColor: Color,
        val bgColor1: Color,
        val bgColor2: Color,
        val cosmetic: Identifier? = null
    ) {
        constructor(color: Color, cosmetic: Identifier? = null): this(color, color.darker(0.2f), color.darker(0.1f), color.darker(0.85f), Color.BLACK, cosmetic);
        constructor(outlineColor1: Color, outlineColor2: Color, cosmetic: Identifier? = null): this(outlineColor1, outlineColor2, (outlineColor1 to outlineColor2).gradient(0.5f), outlineColor1.darker(0.7f), Color.BLACK, cosmetic)
    };

    object Themes {
        private fun id(path: String) = ModMain.id("tooltip/cosmetic/$path");

        val DEFAULT = Theme(Color(0x686868), Color(0x353535));
        val UNCOMMON = Theme(Color.YELLOW.darker(0.2f), Color.YELLOW.darker(0.4f));
        val RARE = Theme(Color.AQUA.darker(0.2f), Color.AQUA.darker(0.4f));
        val EPIC = Theme(Color.LIGHT_PURPLE.darker(0.2f), Color.LIGHT_PURPLE.darker(0.4f));

        val MUSIC = Theme(Color.WHITE, id("music"));
        val ENDER = Theme(Color(0xCA87FF), Color(0x6C11B2), id("ender"));
        val ENCHANT = Theme(Color(0x8B4513), Color(0x723510), id("enchant"));

        val COPPER = Theme(Color(0x70453D), Color(0x542323), id("copper"));
        val GOLD = Theme(Color(0x996922), Color(0x5B3B1D), id("gold"));
        val SILVER = Theme(Color(0x697C8C), Color(0x3A4C61), id("silver"));
    }

    fun drawBackground(context: DrawContext, innerX: IntRange, innerY: IntRange, z: Int, theme: Theme) {
        val xstart = innerX.first - 2;
        val ystart = innerY.first - 2;
        val xend = innerX.endExclusive + 2;
        val yend = innerY.endExclusive + 2;
        val color1 = theme.bgColor1.toInt(240);
        val color2 = theme.bgColor2.toInt(240);

        context.matrices.push();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        // center background
        context.fillGradient(xstart + 1, ystart + 1, xend - 1, yend - 1, z, color1, color2);

        // top background
        context.fill(xstart + 1, ystart, xend - 1, ystart + 1, z, color1);

        // bottom background
        context.fill(xstart + 1, yend - 1, xend - 1, yend, z, color1);

        // left background
        context.fillGradient(xstart, ystart + 1, xstart + 1, yend - 1, z, color1, color2);

        // right background
        context.fillGradient(xend - 1, ystart + 1, xend, yend - 1, z, color1, color2);

        RenderSystem.disableBlend();
        context.matrices.pop();
    }

    fun drawBorder(context: DrawContext, innerX: IntRange, innerY: IntRange, z: Int, theme: Theme) {
        val xstart = innerX.first - 1;
        val ystart = innerY.first - 1;
        val xend = innerX.endExclusive + 1;
        val yend = innerY.endExclusive + 1;
        val xcenter = (xstart + xend - 1) / 2;
        val ycenter = (ystart + yend - 1) / 2;

        val color1 = theme.outlineColor1.toInt();
        val color2 = theme.outlineColor2.toInt();

        context.matrices.push();

        // top
        context.fill(xstart, ystart, xend, ystart + 1, z, color1);

        // bottom
        context.fill(xstart, yend - 1, xend, yend, z, color2);

        // left
        context.fillGradient(xstart, ystart + 1, xstart + 1, yend - 1, z, color1, color2);

        // right
        context.fillGradient(xend - 1, ystart + 1, xend, yend - 1, z, color1, color2);

        // draw cosmetics
        if (theme.cosmetic != null) {
            context.matrices.translate(0f, 0f, 400f);

            // top-left cosmetic
            context.drawGuiTexture(RenderLayer::getGuiTextured, theme.cosmetic, 64, 16, 0, 0, xstart - 3, ystart - 3, 8, 8);

            // bottom-left cosmetic
            context.drawGuiTexture(RenderLayer::getGuiTextured, theme.cosmetic, 64, 16, 0, 8, xstart - 3, (yend - 1) - 4, 8, 8);

            // top-right cosmetic
            context.drawGuiTexture(RenderLayer::getGuiTextured, theme.cosmetic, 64, 16, 56, 0, (xend - 1) - 4, ystart - 3, 8, 8);

            // bottom-right cosmetic
            context.drawGuiTexture(RenderLayer::getGuiTextured, theme.cosmetic, 64, 16, 56, 8, (xend - 1) - 4, (yend - 1) - 4, 8, 8);

            // top-center cosmetic
            context.drawGuiTexture(RenderLayer::getGuiTextured, theme.cosmetic, 64, 16, 8, 0, xcenter - 23, ystart - 6, 48, 8);

            // bottom-center cosmetic
            context.drawGuiTexture(RenderLayer::getGuiTextured, theme.cosmetic, 64, 16, 8, 8, xcenter - 23, (yend - 1) - 1, 48, 8);
        }

        context.matrices.pop();
    }
}