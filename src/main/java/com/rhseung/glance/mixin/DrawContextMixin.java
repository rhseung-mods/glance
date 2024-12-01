package com.rhseung.glance.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.rhseung.glance.tooltip.AttributeTooltip;
import com.rhseung.glance.util.Draw;
import com.rhseung.glance.util.Util;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Unique
    private static int margin = 5;
    @Unique
    private static int paddingBetweenTooltipAndText = 9;

    @Unique
    private static boolean usePadding(List<TooltipComponent> components) {
        int isText = 0;
        int isNotText = 0;

        for (TooltipComponent tooltip : components) {
            if (tooltip instanceof OrderedTextTooltipComponent)
                isText++;
            else
                isNotText++;
        }

        return isText >= 2 && isNotText >= 1;
    }

    @Redirect(
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;render(Lnet/minecraft/client/gui/DrawContext;IIIIILnet/minecraft/util/Identifier;)V")
    )
    public void renderTooltipBackground(
        DrawContext context,
        int x,
        int y,
        int width,
        int height,
        int z,
        Identifier texture,
        @Local(argsOnly = true) List<TooltipComponent> components
    ) {
        TooltipBackgroundRenderer.render(
            context,
            x,
            y,
            width + (!components.stream().map(c -> c instanceof AttributeTooltip).toList().isEmpty() ? Draw.ICON_START_PADDING : 0),
            height + ((components.size() <= 1) ? 0 : margin + (usePadding(components) ? paddingBetweenTooltipAndText : 0)),
            z,
            texture
        );
    }

    @Redirect(
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawItems(Lnet/minecraft/client/font/TextRenderer;IIIILnet/minecraft/client/gui/DrawContext;)V")
    )
    public void drawTitleAndSeparator(
        TooltipComponent component,
        TextRenderer textRenderer,
        int x,
        int y,
        int tooltipWidth,
        int tooltipHeight,
        DrawContext context,
        @Local(ordinal = 9) LocalIntRef q,
        @Local(ordinal = 10) int r,
        @Local(ordinal = 0, argsOnly = true) @Nullable Identifier texture,
        @Local(ordinal = 0, argsOnly = true) List<TooltipComponent> components
    ) {
        var id = texture != null ? texture.getPath() : "common";
        var rarity = Rarity.valueOf(id.toUpperCase());
        var color = rarity.getFormatting().getColorValue();

        component.drawItems(textRenderer, x, y, tooltipWidth, tooltipHeight + margin, context);

        if (components.size() > 1 && r == 0) {
            Util.INSTANCE.toTooltipFrameSeparator(rarity)
                .draw(context, x, x + tooltipWidth, y + component.getHeight(textRenderer) + margin / 2);

            q.set(q.get() + margin);
        }
    }

    @Redirect(
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawText(Lnet/minecraft/client/font/TextRenderer;IILorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V")
    )
    public void drawTextShift(
        TooltipComponent component,
        TextRenderer textRenderer,
        int x,
        int y,
        Matrix4f matrix,
        VertexConsumerProvider.Immediate vertexConsumers,
        @Local(ordinal = 9) LocalIntRef q,
        @Local(ordinal = 10) int r,
        @Local(ordinal = 0, argsOnly = true) @Nullable Identifier texture,
        @Local(ordinal = 0, argsOnly = true) List<TooltipComponent> components
    ) {
        if (r == 1)
            q.set(q.get() + margin + (usePadding(components) ? paddingBetweenTooltipAndText : 0));

        component.drawText(textRenderer, x, q.get(), matrix, vertexConsumers);
    }

}
