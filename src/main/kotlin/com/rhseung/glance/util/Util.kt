package com.rhseung.glance.util

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
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

    @Throws(NoSuchFieldException::class)
    private fun getField(clazz: Class<*>, fieldName: String): Field {
        return try {
            clazz.getDeclaredField(fieldName);
        } catch (e: NoSuchFieldException) {
            val superClass = clazz.superclass;
            if (superClass == null) {
                throw e;
            } else {
                getField(superClass, fieldName);
            }
        }
    }

    fun <T> get(receiver: Any, propertyName: String): T {
        val clazz: Class<*> = receiver.javaClass

        var field: Field? = null
        try {
            field = getField(clazz, propertyName)
        } catch (e: NoSuchFieldException) {
            throw e
        }

        field.isAccessible = true

        return try {
            @Suppress("UNCHECKED_CAST")
            field[receiver] as T
        } catch (e: IllegalAccessException) {
            throw e
        } catch (e: ClassCastException) {
            throw e
        }
    }

    fun <T> Any.getProperty(propertyName: String): T {
        return get(this, propertyName);
    }

    @JvmName("getOperator")
    operator fun <T> Any.get(propertyName: String): T {
        return getProperty<T>(propertyName);
    }

    @Throws(NoSuchMethodException::class)
    private fun getMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method {
        val methods = clazz.declaredMethods.filter { it.name == methodName && it.parameterCount == parameterTypes.size };

        if (methods.isEmpty()) {
            val superClass = clazz.superclass;
            if (superClass == null)
                throw NoSuchMethodException();
            else
                return getMethod(superClass, methodName, *parameterTypes);
        }

        if (methods.size == 1)
            return methods[0];

        for (method in methods) {
            if (method.parameterTypes.contentEquals(parameterTypes))
                return method;
        }

        throw NoSuchMethodException();
    }

    fun <T> invoke(receiver: Any, methodName: String, vararg args: Any?): T {
        val clazz: Class<*> = receiver.javaClass

        // Determine the parameter types of the method
        val parameterTypes = args.map { it?.javaClass ?: Any::class.java }.toTypedArray()

        val method: Method
        try {
            method = getMethod(clazz, methodName, *parameterTypes)
        } catch (e: NoSuchMethodException) {
            throw e
        }

        method.isAccessible = true

        return try {
            @Suppress("UNCHECKED_CAST")
            method.invoke(receiver, *args) as T
        } catch (e: IllegalAccessException) {
            throw e
        } catch (e: ClassCastException) {
            throw e
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }

    fun <T> Any.invokeMethod(methodName: String, vararg args: Any?): T {
        return invoke(this, methodName, *args)
    }

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
        val guiAtlasManager = this.getProperty<GuiAtlasManager>("guiAtlasManager");
        val sprite2 = guiAtlasManager.getSprite(sprite);
        val scaling = guiAtlasManager.getScaling(sprite2);

        if (scaling is Stretch)
            this.invokeMethod<Unit>("drawSpriteRegion", renderLayers, sprite2, textureWidth, textureHeight, u, v, x, y, width, height, color);
        else
            this.invokeMethod<Unit>("drawSpriteStretched", renderLayers, sprite2, x, y, width, height, color);
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
        val guiAtlasManager = this.getProperty<GuiAtlasManager>("guiAtlasManager");
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
        val guiAtlasManager = this.getProperty<GuiAtlasManager>("guiAtlasManager");
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
        val vertexConsumer = this.getProperty<VertexConsumerProvider.Immediate>("vertexConsumers").getBuffer(renderLayer);

        vertexConsumer.vertex(matrix, x1, y1, 0f).texture(u1, v1).color(color);
        vertexConsumer.vertex(matrix, x1, y2, 0f).texture(u1, v2).color(color);
        vertexConsumer.vertex(matrix, x2, y2, 0f).texture(u2, v2).color(color);
        vertexConsumer.vertex(matrix, x2, y1, 0f).texture(u2, v1).color(color);
    }
}