package com.rhseung.glance.draw

import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.getProperty
import com.rhseung.glance.util.Util.invokeMethod
import com.rhseung.glance.util.Util.modify
import com.rhseung.glance.util.Util.size
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.texture.GuiAtlasManager
import net.minecraft.client.texture.Scaling.Stretch
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import java.util.function.Function

object DrawHelper {
    val DOT = ModMain.id("textures/frame/default.png");     // .png 안 붙이면 오류 남
    val BACKGROUND = ModMain.id("tooltip/background");

    fun drawHorizontalLine(context: DrawContext, xRange: IntRange, y: Int, z: Int, color: Color = Color.WHITE) {        
        assert(!xRange.isEmpty()) { "Range must not be empty" };

        for (x in xRange) {
            context.drawTexture(RenderLayer::getGuiTextured, DOT, x, y, 0f, 0f, 1, 1, 1, 1, color.toInt(true));
        }
    }

    fun drawHorizontalLine(context: DrawContext, xRange: IntRange, y: Int, colorFactory: (Float) -> Color) {
        assert(!xRange.isEmpty()) { "Range must not be empty" };

        for (x in xRange) {
            val ratio = (x - xRange.first).toFloat() / xRange.size();
            context.drawTexture(RenderLayer::getGuiTextured, DOT, x, y, 0f, 0f, 1, 1, 1, 1, colorFactory(ratio).toInt(true));
        }
    }

    fun drawVerticalLine(context: DrawContext, yRange: IntRange, x: Int, z: Int, color: Color = Color.WHITE) {
        assert(!yRange.isEmpty()) { "Range must not be empty" };

        for (y in yRange) {
            context.drawTexture(RenderLayer::getGuiTextured, DOT, x, y, 0f, 0f, 1, 1, 1, 1, color.toInt(true));
        }
    }

    fun drawVerticalLine(context: DrawContext, yRange: IntRange, x: Int, z: Int, colorFactory: (Float) -> Color) {
        assert(!yRange.isEmpty()) { "Range must not be empty" };

        for (y in yRange) {
            val ratio = (y - yRange.first).toFloat() / yRange.size();
            context.drawTexture(RenderLayer::getGuiTextured, DOT, x, y, 0f, 0f, 1, 1, 1, 1, colorFactory(ratio).toInt(true));
        }
    }

    fun drawBorder(context: DrawContext, xRange: IntRange, yRange: IntRange, z: Int, color: Color = Color.WHITE) {
        drawHorizontalLine(context, xRange.modify(1, -1), yRange.first, z, color);
        drawHorizontalLine(context, xRange.modify(1, -1), yRange.last, z, color);
        drawVerticalLine(context, yRange.modify(1, -1), xRange.first, z, color);
        drawVerticalLine(context, yRange.modify(1, -1), xRange.last, z, color);
    }

    fun drawBorder(context: DrawContext, xRange: IntRange, yRange: IntRange, z: Int, colorFactory: (Float) -> Color) {
        drawHorizontalLine(context, xRange.modify(1, -1), yRange.first, z, colorFactory(0f));
        drawHorizontalLine(context, xRange.modify(1, -1), yRange.last, z, colorFactory(1f));
        drawVerticalLine(context, yRange.modify(1, -1), xRange.first, z, colorFactory);
        drawVerticalLine(context, yRange.modify(1, -1), xRange.last, z, colorFactory);
    }
//    fun drawTexture()

    fun drawSprite(context: DrawContext, sprite: Identifier, xRange: IntRange, yRange: IntRange, z: Int, color: Color = Color.WHITE) {
        assert(!xRange.isEmpty() && !yRange.isEmpty()) { "Width and height must be non-negative" };

        // TODO: context.matrices.push();
        
//        context.matrices.push();
        context.matrices.translate(0f, 0f, z.toFloat());
        context.drawGuiTexture(RenderLayer::getGuiTextured, sprite, xRange.first, yRange.first, xRange.size(),yRange.size(), color.toInt(true));
//        context.matrices.pop();
    }

    fun drawItem(context: DrawContext, stack: ItemStack, x0: Int, y0: Int, z: Int, size: Int = 16) {
        assert(size >= 0) { "Size must be non-negative" };

        val ratio = size.toFloat() / 16;

//        context.matrices.push();
        context.matrices.scale(ratio, ratio, 1f);
        context.matrices.translate(0f, 0f, z.toFloat());
        context.drawItem(stack, x0, y0);
        context.matrices.scale(1 / ratio, 1 / ratio, 1f);
//        context.matrices.pop();
    }

    fun drawGuiTextureColor(
        context: DrawContext,
        renderLayers: Function<Identifier, RenderLayer>,
        sprite: Identifier,
        textureWidth: Int,
        textureHeight: Int,
        u: Int,
        v: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        color: Int
    ) {
        val guiAtlasManager = context.getProperty<GuiAtlasManager>("guiAtlasManager");
        val sprite2 = guiAtlasManager.getSprite(sprite);
        val scaling = guiAtlasManager.getScaling(sprite2);

        if (scaling is Stretch)
            context.invokeMethod<Unit>("drawSpriteRegion", renderLayers, sprite2, textureWidth, textureHeight, u, v, x, y, width, height, color);
        else
            context.invokeMethod<Unit>("drawSpriteStretched", renderLayers, sprite2, x, y, width, height, color);
    }
}