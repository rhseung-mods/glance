package com.rhseung.glance.tooltip

import com.mojang.blaze3d.systems.RenderSystem
import com.rhseung.glance.mixin.accessor.OrderedTextTooltipComponentAccessor
import com.rhseung.glance.tooltip.TooltipConstants.Padding.TOOLTIP_FRAME_MARGIN
import com.rhseung.glance.tooltip.component.GlanceTooltipComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.tooltip.component.YPaddingComponent
import com.rhseung.glance.tooltip.content.FloatingTooltipContent
import com.rhseung.glance.tooltip.content.TooltipContentRegistry
import com.rhseung.glance.tooltip.template.DefaultTooltip
import com.rhseung.glance.tooltip.template.DetailTooltip
import com.rhseung.glance.tooltip.template.GlanceTooltip
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util
import com.rhseung.glance.util.Util.toRangeSize
import com.rhseung.glance.util.Util.toText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.gui.tooltip.TooltipPositioner
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.ItemStack
import net.minecraft.text.Style
import net.minecraft.text.Text

object Tooltip {
    fun getTooltip(
        tooltipComponents: List<TooltipComponent>,
        stack: ItemStack? = null,
        canDetailTooltip: Boolean = true,
        theme: TooltipDecor.Theme? = null
    ): GlanceTooltip? {
        val originalComponents: List<TooltipComponent> = tooltipComponents.map { component ->
            if (component is OrderedTextTooltipComponent)
                TextComponent((component as OrderedTextTooltipComponentAccessor).text.toText());
            else
                component;
        };

        val titleStyle: Style? = Util.getStyle((originalComponents[0] as? TextComponent)?.text);

        val titleEndIdx: Int = originalComponents
            .indexOfFirst { it !is TextComponent || Util.getStyle(it.text) != titleStyle }
            .takeIf { it != -1 } ?: originalComponents.size;

        val titleComponents: List<TextComponent> = originalComponents
            .subList(0, titleEndIdx)
            .map { it as TextComponent };

        if (titleComponents.isEmpty())
            return null;

        val components: MutableList<TooltipComponent> = originalComponents
            .subList(titleEndIdx, originalComponents.size)
            .dropWhile { it is TextComponent && it.text.string.isEmpty() }
            .toMutableList();

        val floatingComponents: MutableList<GlanceTooltipComponent> = mutableListOf();

        val isDetail = stack != null && Screen.hasShiftDown() && canDetailTooltip;

        if (stack != null) {
            val contents = TooltipContentRegistry.find(stack.item, stack);
            val contentsComponents = mutableListOf<GlanceTooltipComponent>();

            contents.forEach { content ->
                val eachComponents: List<LineComponent> = if (isDetail) content.getShiftComponents() else content.getComponents();

                if (eachComponents.isNotEmpty()) {
                    if (content is FloatingTooltipContent) {
                        floatingComponents.addAll(eachComponents);
                    }
                    else {
                        if (contentsComponents.isNotEmpty() && (eachComponents.size > 1 || isDetail))
                            contentsComponents.add(YPaddingComponent(8));

                        contentsComponents.addAll(eachComponents);
                    }
                }
            };

            if (contentsComponents.isNotEmpty()) {
                if (isDetail) {
                    if (components.isNotEmpty())
                        components.add(YPaddingComponent(8));

                    components.addAll(contentsComponents);
                }
                else {
                    val front = mutableListOf(*contentsComponents.toTypedArray());
                    if (components.isNotEmpty())
                        front.add(YPaddingComponent(8));

                    components.addAll(0, front);
                }
            }
        }

        val color: Color? = titleStyle?.color?.rgb?.let(::Color);

        val theme: TooltipDecor.Theme =
            if (theme != null)
                theme;
            else if (stack != null)
                TooltipDecor.themeFromItem(stack);
            else if (color != null && color != Color.WHITE)
                TooltipDecor.Theme(color);
            else
                TooltipDecor.Themes.DEFAULT;

        return if (isDetail)
            DetailTooltip(titleComponents, components, floatingComponents, theme, stack);
        else
            DefaultTooltip(titleComponents, components, floatingComponents, theme);
    }

    fun drawBackground(
        context: DrawContext,
        innerX: IntRange,
        innerY: IntRange,
        z: Int,
        theme: TooltipDecor.Theme
    ) {
        val xstart = innerX.first - 2;
        val ystart = innerY.first - 2;
        val xend = innerX.endExclusive + 2;
        val yend = innerY.endExclusive + 2;
        val color1 = theme.topOfBackground.toInt(240);
        val color2 = theme.bottomOfBackground.toInt(240);

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
        context.fill(xstart + 1, yend - 1, xend - 1, yend, z, color2);

        // left background
        context.fillGradient(xstart, ystart + 1, xstart + 1, yend - 1, z, color1, color2);

        // right background
        context.fillGradient(xend - 1, ystart + 1, xend, yend - 1, z, color1, color2);

        RenderSystem.disableBlend();
        context.matrices.pop();
    }

    fun drawBorder(
        context: DrawContext,
        innerX: IntRange,
        innerY: IntRange,
        z: Int,
        theme: TooltipDecor.Theme
    ) {
        val xstart = innerX.first - 1;
        val ystart = innerY.first - 1;
        val xend = innerX.endExclusive + 1;
        val yend = innerY.endExclusive + 1;
        val xcenter = (xstart + xend - 1) / 2;
        val ycenter = (ystart + yend - 1) / 2;

        val color1 = theme.topOfOutline.toInt();
        val color2 = theme.bottomOfOutline.toInt();

        context.matrices.push();

        // top
        context.fill(xstart + 1, ystart, xend - 1, ystart + 1, z, color1);

        // bottom
        context.fill(xstart + 1, yend - 1, xend - 1, yend, z, color2);

        // left
        context.fillGradient(xstart, ystart + 1, xstart + 1, yend - 1, z, color1, color2);

        // right
        context.fillGradient(xend - 1, ystart + 1, xend, yend - 1, z, color1, color2);

        // draw cosmetics
        if (theme.cosmetic != null) {
            context.matrices.translate(0f, 0f, 400f);

            // top-left cosmetic
            context.drawGuiTexture(
                RenderLayer::getGuiTextured,
                theme.cosmetic,
                64,
                16,
                0,
                0,
                xstart - 3,
                ystart - 3,
                8,
                8
            );

            // bottom-left cosmetic
            context.drawGuiTexture(
                RenderLayer::getGuiTextured,
                theme.cosmetic,
                64,
                16,
                0,
                8,
                xstart - 3,
                (yend - 1) - 4,
                8,
                8
            );

            // top-right cosmetic
            context.drawGuiTexture(
                RenderLayer::getGuiTextured,
                theme.cosmetic,
                64,
                16,
                56,
                0,
                (xend - 1) - 4,
                ystart - 3,
                8,
                8
            );

            // bottom-right cosmetic
            context.drawGuiTexture(
                RenderLayer::getGuiTextured,
                theme.cosmetic,
                64,
                16,
                56,
                8,
                (xend - 1) - 4,
                (yend - 1) - 4,
                8,
                8
            );

            // top-center cosmetic
            context.drawGuiTexture(
                RenderLayer::getGuiTextured,
                theme.cosmetic,
                64,
                16,
                8,
                0,
                xcenter - 23,
                ystart - 6,
                48,
                8
            );

            // bottom-center cosmetic
            context.drawGuiTexture(
                RenderLayer::getGuiTextured,
                theme.cosmetic,
                64,
                16,
                8,
                8,
                xcenter - 23,
                (yend - 1) - 1,
                48,
                8
            );
        }

        context.matrices.pop();
    }

    fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        positioner: TooltipPositioner,
        mouseX: Int,
        mouseY: Int,
        tooltipComponents: List<TooltipComponent>,
        stack: ItemStack? = null,
        canDetailTooltip: Boolean = true
    ) {
        val tooltip = getTooltip(tooltipComponents, stack, canDetailTooltip) ?: return;

        this.draw(
            context,
            textRenderer,
            positioner,
            mouseX,
            mouseY,
            tooltip
        );
    }

    fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        mouseX: Int,
        mouseY: Int,
        stack: ItemStack,
        canDetailTooltip: Boolean = true
    ) {
        val components: MutableList<TooltipComponent> = Screen
            .getTooltipFromItem(MinecraftClient.getInstance(), stack)
            .map(::TextComponent)
            .toMutableList();

        if (stack.tooltipData.isPresent)
            components.add(TooltipComponent.of(stack.tooltipData.get()));

        this.draw(context, textRenderer, HoveredTooltipPositioner.INSTANCE, mouseX, mouseY, components, stack, canDetailTooltip);
    }

    fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        mouseX: Int,
        mouseY: Int,
        text: Text,
    ) {
        this.draw(context, textRenderer, HoveredTooltipPositioner.INSTANCE, mouseX, mouseY, listOf(TextComponent(text)), null, false);
    }

    fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        positioner: TooltipPositioner,
        mouseX: Int,
        mouseY: Int,
        tooltip: GlanceTooltip
    ) {
        val width = tooltip.getWidth(textRenderer);
        val height = tooltip.getHeight(textRenderer);
        val tooltipWidth = width + 2 * TOOLTIP_FRAME_MARGIN;
        val tooltipHeight = height + 2 * TOOLTIP_FRAME_MARGIN;

        val vector2ic = positioner.getPosition(
            context.scaledWindowWidth,
            context.scaledWindowHeight,
            mouseX,
            mouseY,
            tooltipWidth,
            tooltipHeight
        );
        val x = vector2ic.x();
        val y = vector2ic.y();
        val z = 400;

        val innerX = (x + TOOLTIP_FRAME_MARGIN).toRangeSize(width);
        val innerY = (y + TOOLTIP_FRAME_MARGIN).toRangeSize(height);

        drawBackground(context, innerX, innerY, z, tooltip.theme);
        drawBorder(context, innerX, innerY, z, tooltip.theme);

        context.matrices.push();
        context.matrices.translate(0f, 0f, z.toFloat());
        tooltip.draw(context, textRenderer, innerX.first, innerY.first, x, y);
        context.matrices.pop();
    }
}