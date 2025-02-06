package com.rhseung.glance.util

import com.rhseung.glance.mixin.accessor.DrawContextAccessor
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.texture.GuiAtlasManager
import net.minecraft.client.texture.Scaling.Stretch
import net.minecraft.client.texture.Sprite
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.joml.Matrix4f
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.function.Function
import kotlin.math.abs
import kotlin.math.ceil

object Util {
    fun Double.roundTo(n: Int) = "%.${n}f".format(this).toDouble();

    fun Float.roundTo(n: Int) = "%.${n}f".format(this).toFloat();

    fun Double.toStringPretty() = this.roundTo(2).toString().removeSuffix("0").removeSuffix(".");

    fun Float.toStringPretty() = this.roundTo(2).toString().removeSuffix("0").removeSuffix(".");

    fun Int.toRangeSize(size: Int) = this..<(this + size);

    fun IntRange.modify(startDelta: Int, endDelta: Int) = (this.first + startDelta)..(this.last + endDelta);

    fun IntRange.size() = this.endExclusive - this.first;

    fun IntProgression.size() = abs((this.last - this.first) / this.step) + 1;

    fun IntRange.splitToTwo(): Pair<IntRange, IntRange> {
        val mid = (this.first + this.last) / 2;
        return this.first..mid to (mid + 1)..this.last;
    }

    fun <T> Boolean.ifElse(ifTrue: T, ifFalse: T): T = if (this) ifTrue else ifFalse;

    fun Text.toMutableText(): MutableText = this.copy();

    fun String.titlecase(): String {
        return this.lowercase().split(Regex(" +")).joinToString(" ") {
            it.split("_").joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
        };
    }

    fun getStyle(text: Text?): Style? {
        if (text == null)
            return null;

        return text.style.takeIf { !it.isEmpty }
            ?: text.siblings.getOrNull(0)?.style.takeIf { it != null && !it.isEmpty };
    }

    fun Boolean.toInt() = if (this) 1 else 0;

    inline fun <T, R> Iterable<T>.mapRight(transform: (T) -> R): List<R> {
        val list = mutableListOf<R>()
        for (item in this.reversed()) {
            list.add(0, transform(item));
        }
        return list;
    }

    inline fun <T, R> Iterable<T>.forEachRight(action: (T) -> R) {
        for (item in this.reversed()) {
            action(item);
        }
    }

    fun <T, R: T> Iterable<T>.joinTo(separator: R): List<T> {
        val list = mutableListOf<T>()
        for (item in this) {
            list.add(item);
            list.add(separator);
        }
        if (list.isNotEmpty())
            list.removeLast();
        return list;
    }

    fun ceilToInt(value: Float) = ceil(value).toInt();

    fun ceilToInt(value: Double) = ceil(value).toInt();

    fun OrderedText.toText(): Text {
        val mutableTextArr = mutableListOf(Text.empty());
        this.accept { idx, style, c ->
            mutableTextArr[0] = mutableTextArr[0].append(Text.literal(Character.toString(c)).setStyle(style));
            true;
        }
        return mutableTextArr[0];
    }

    fun DrawContext.drawGuiTextureColor(
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
        val guiAtlasManager = (this as DrawContextAccessor).guiAtlasManager;
        val sprite2 = guiAtlasManager.getSprite(sprite);
        val scaling = guiAtlasManager.getScaling(sprite2);

        if (scaling is Stretch)
            (this as DrawContextAccessor).drawSpriteRegionMixin(renderLayers, sprite2, textureWidth, textureHeight, u, v, x, y, width, height, color);
        else
            (this as DrawContextAccessor).drawSpriteStretchedMixin(renderLayers, sprite2, x, y, width, height, color);
    }

    fun DrawContext.drawGuiTextureFloat(
        renderLayers: Function<Identifier, RenderLayer>,
        sprite: Identifier,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Int = -1
    ) {
        val guiAtlasManager = (this as DrawContextAccessor).guiAtlasManager;
        val sprite: Sprite = guiAtlasManager.getSprite(sprite);

        val d: Float = 0b1 / 32768f;
        this.drawTextureQuadFloat(renderLayers, sprite.atlasId, x, x + width, y, y + height,
            sprite.minU + d, sprite.maxU + d, sprite.minV - d, sprite.maxV - d, color);
    }

    fun DrawContext.drawGuiTextureFloat(
        renderLayers: Function<Identifier, RenderLayer>,
        sprite: Identifier,
        textureWidth: Float,
        textureHeight: Float,
        u: Float,
        v: Float,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Int = -1
    ) {
        val guiAtlasManager = (this as DrawContextAccessor).guiAtlasManager;
        val sprite: Sprite = guiAtlasManager.getSprite(sprite);

        val d: Float = 0b1 / 32768f;
        if (width != 0f && height != 0f) {
            this.drawTextureQuadFloat(
                renderLayers,
                sprite.atlasId,
                x,
                x + width,
                y,
                y + height,
                sprite.getFrameU(u / textureWidth) + d,
                sprite.getFrameU((u + width) / textureWidth) + d,
                sprite.getFrameV(v / textureHeight) - d,
                sprite.getFrameV((v + height) / textureHeight) - d,
                color
            )
        }
    }

    fun DrawContext.drawTextureQuadFloat(
        renderLayers: Function<Identifier, RenderLayer>,
        texture: Identifier,
        x1: Float,
        x2: Float,
        y1: Float,
        y2: Float,
        u1: Float,
        u2: Float,
        v1: Float,
        v2: Float,
        color: Int
    ) {
        val renderLayer: RenderLayer = renderLayers.apply(texture);
        val matrix: Matrix4f = this.matrices.peek().positionMatrix;
        val vertexConsumer: VertexConsumer = (this as DrawContextAccessor).vertexConsumers.getBuffer(renderLayer);

        vertexConsumer.vertex(matrix, x1, y1, 0f).texture(u1, v1).color(color);
        vertexConsumer.vertex(matrix, x1, y2, 0f).texture(u1, v2).color(color);
        vertexConsumer.vertex(matrix, x2, y2, 0f).texture(u2, v2).color(color);
        vertexConsumer.vertex(matrix, x2, y1, 0f).texture(u2, v1).color(color);
    }
}