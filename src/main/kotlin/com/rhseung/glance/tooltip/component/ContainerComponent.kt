package com.rhseung.glance.tooltip.component

import com.rhseung.glance.ModMain
import com.rhseung.glance.tooltip.Tooltip
import com.rhseung.glance.tooltip.TooltipDecor
import com.rhseung.glance.tooltip.template.GlanceTooltip
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.toInt
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

open class ContainerComponent(
    val stacks: List<ItemStack>,
    val minRow: Int,
    val maxColumn: Int,
    val color: Int = -1,
    val selectedStackIndex: Int = -1
) : FloatingTooltipComponent {
    private val CONTAINER_TEXTURE: Identifier = ModMain.id("textures/gui/container.png");

    val size = stacks.size;
    val row = (size / maxColumn + (size % maxColumn != 0).toInt()).coerceAtLeast(minRow);
    val col = size.coerceAtMost(maxColumn);

    class NineSlice(val u: Int, val v: Int, val width: Int, val height: Int) {
        fun draw(context: DrawContext, texture: Identifier, x: Int, y: Int, color: Int) {
            context.drawTexture(
                RenderLayer::getGuiTextured,
                texture,
                x,
                y,
                u.toFloat(),
                v.toFloat(),
                width,
                height,
                256,
                256,
                color
            );
        }
    };

    protected open val nineSlices = listOf(
        NineSlice(0, 0, 5, 5), NineSlice(5, 0, 18, 5), NineSlice(23, 0, 5, 5),
        NineSlice(0, 5, 5, 18), NineSlice(5, 5, 18, 18), NineSlice(23, 5, 5, 18),
        NineSlice(0, 23, 5, 5), NineSlice(5, 23, 18, 5), NineSlice(23, 23, 5, 5),
    );
    protected open val background = NineSlice(28, 5, 18, 18);

    override fun getWidthExact(textRenderer: TextRenderer): Int {
        return 5 + 18 * col + 5;
    }

    override fun getHeightExact(textRenderer: TextRenderer): Int {
        return 5 + 18 * row + 5;
    }

    open fun drawBackground(context: DrawContext, textRenderer: TextRenderer, x0: Int, y0: Int) {
        var x = x0;
        var y = y0;

        nineSlices[0].draw(context, CONTAINER_TEXTURE, x, y, color);
        x += nineSlices[0].width;
        repeat(col) {
            nineSlices[1].draw(context, CONTAINER_TEXTURE, x, y, color);
            x += nineSlices[1].width;
        }
        nineSlices[2].draw(context, CONTAINER_TEXTURE, x, y, color);
        x = x0;
        y += nineSlices[0].height;

        repeat(row) { i ->
            nineSlices[3].draw(context, CONTAINER_TEXTURE, x, y, color);
            x += nineSlices[3].width;
            repeat(col) { j ->
//                (i * col + j < size).ifElse(nineSlices[4], background).draw(context, x, y);
                nineSlices[4].draw(context, CONTAINER_TEXTURE, x, y, color);
                x += nineSlices[4].width;
            }
            nineSlices[5].draw(context, CONTAINER_TEXTURE, x, y, color);
            x = x0;
            y += nineSlices[3].height;
        }

        nineSlices[6].draw(context, CONTAINER_TEXTURE, x, y, color);
        x += nineSlices[6].width;
        repeat(col) {
            nineSlices[7].draw(context, CONTAINER_TEXTURE, x, y, color);
            x += nineSlices[7].width;
        }
        nineSlices[8].draw(context, CONTAINER_TEXTURE, x, y, color);
    }

    open fun drawItems(context: DrawContext, textRenderer: TextRenderer, x0: Int, y0: Int) {
        var x = x0 + nineSlices[0].width;
        var y = y0 + nineSlices[0].height;

        for (i in 0..<row) {
            for (j in 0..<col) {
                if (i * col + j < size) {
                    val stack = stacks[i * col + j];
                    if (!stack.isEmpty) {
                        context.drawItem(stack, x + 1, y + 1);
                        context.drawStackOverlay(textRenderer, stack, x + 1, y + 1);
                        if (i * col + j == selectedStackIndex) {
                            context.fill(x + 1, y + 1, x + 17, y + 17, 200, Color.WHITE.toInt(70));

                            val tooltip: GlanceTooltip? = Tooltip.getTooltip(
                                listOf(TextComponent(stack.name)),
                                null,
                                false,
                                TooltipDecor.Theme(Color(color))
                            );

                            if (tooltip != null) {
                                val width = tooltip.getWidth(textRenderer);
                                val height = tooltip.getHeight(textRenderer);

                                Tooltip.draw(
                                    context,
                                    textRenderer,
                                    HoveredTooltipPositioner.INSTANCE,
                                    x - 2 + nineSlices[4].width / 2 - 12 - width / 2,
                                    y + 12 - height - 2,
                                    tooltip
                                );
                            }
                        }
                    }
                }
                x += nineSlices[4].width;
            }
            x = x0 + nineSlices[0].width;
            y += nineSlices[4].height;
        }
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int,
        outerX: Int,
        outerY: Int
    ) {
        this.drawBackground(context, textRenderer, x0, y0);
        this.drawItems(context, textRenderer, x0, y0);
    }
}