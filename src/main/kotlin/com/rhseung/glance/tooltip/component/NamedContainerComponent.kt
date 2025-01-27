package com.rhseung.glance.tooltip.component

import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.toInt
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.math.ceil

class NamedContainerComponent(
    val title: Text,
    stacks: List<ItemStack>,
    row: Int,
    col: Int,
    color: Int = -1,
    selectedStackIndex: Int = -1
) : ContainerComponent(stacks, row, col, color, selectedStackIndex) {
    private val CONTAINER_TEXTURE: Identifier = ModMain.id("textures/gui/named_container.png");

    override val nineSlices = listOf(
        NineSlice(0, 0, 5, 17), NineSlice(5, 0, 18, 17), NineSlice(23, 0, 5, 17),
        NineSlice(0, 17, 5, 18), NineSlice(5, 17, 18, 18), NineSlice(23, 17, 5, 18),
        NineSlice(0, 35, 5, 5), NineSlice(5, 35, 18, 5), NineSlice(23, 35, 5, 5),
    );
    override val background = NineSlice(28, 17, 18, 18);

    override fun getWidth(textRenderer: TextRenderer): Int {
        val textCol = ceil(textRenderer.getWidth(title) / 18f).toInt();
        return 5 + (18 * maxOf(col, textCol)) + 5;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return 17 + 18 * row + 5;
    }

    override fun drawBackground(context: DrawContext, textRenderer: TextRenderer, x0: Int, y0: Int) {
        var x = x0;
        var y = y0;
        val textCol = ceil(textRenderer.getWidth(title) / 18f).toInt();

        nineSlices[0].draw(context, CONTAINER_TEXTURE, x, y, color);
        x += nineSlices[0].width;
        repeat(maxOf(col, textCol)) {
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
        repeat(maxOf(col, textCol)) {
            nineSlices[7].draw(context, CONTAINER_TEXTURE, x, y, color);
            x += nineSlices[7].width;
        }
        nineSlices[8].draw(context, CONTAINER_TEXTURE, x, y, color);
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
        super.draw(context, textRenderer, innerWidth, innerHeight, x0, y0, outerX, outerY);

        val color = Color(color);
        context.drawText(textRenderer, title, x0 + 6, y0 + 6, Color(0, 0f, 1f - color.V).toInt(), false);
    }
}