package com.rhseung.glance.mixin.tooltip;

import com.rhseung.glance.tooltip.Tooltip;
import com.rhseung.glance.tooltip.component.TextComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow
    public abstract void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int z, int color);

    @Inject(
            method = "drawItemTooltip",
            at = @At("HEAD"),
            cancellable = true
    )
    public void drawItemTooltipMixin(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        List<TooltipComponent> components = new java.util.ArrayList<>();
        List<Text> texts = Screen.getTooltipFromItem(MinecraftClient.getInstance(), stack);

        if (!texts.isEmpty())
            components.addAll(texts.stream().map(TextComponent::new).toList());
        if (stack.getTooltipData().isPresent())
            components.add(TooltipComponent.of(stack.getTooltipData().get()));

        DrawContext context = (DrawContext) (Object) this;
        Tooltip.INSTANCE.draw(context, textRenderer, HoveredTooltipPositioner.INSTANCE, x, y, components, stack);

        ci.cancel();
    }

    @Inject(
            method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/util/Identifier;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void drawTooltipMixin(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, Identifier texture, CallbackInfo ci) {
        List<TooltipComponent> components = new java.util.ArrayList<>();

        if (!text.isEmpty())
            components.addAll(text.stream().map(TextComponent::new).toList());

        data.ifPresent(tooltipData -> components.add(TooltipComponent.of(tooltipData)));

        DrawContext context = (DrawContext) (Object) this;
        Tooltip.INSTANCE.draw(context, textRenderer, HoveredTooltipPositioner.INSTANCE, x, y, components, null);

        ci.cancel();
    }

    @Inject(
            method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IILnet/minecraft/util/Identifier;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void drawTooltipMixin(TextRenderer textRenderer, Text text, int x, int y, Identifier texture, CallbackInfo ci) {
        drawTooltipMixin(textRenderer, List.of(text), x, y, texture, ci);
    }

    @Inject(
            method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/util/Identifier;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void drawTooltipMixin(TextRenderer textRenderer, List<Text> text, int x, int y, Identifier texture, CallbackInfo ci) {
        List<TooltipComponent> components = new java.util.ArrayList<>(text.stream().map(TextComponent::new).toList());

        DrawContext context = (DrawContext) (Object) this;
        Tooltip.INSTANCE.draw(context, textRenderer, HoveredTooltipPositioner.INSTANCE, x, y, components, null);

        ci.cancel();
    }

    @Inject(
            method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void drawTooltipMixin(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, Identifier texture, CallbackInfo ci) {
        DrawContext context = (DrawContext) (Object) this;

        Tooltip.INSTANCE.draw(context, textRenderer, positioner, x, y, components, null);

        ci.cancel();
    }

    @Inject(
            method = "drawItemBar",
            at = @At("HEAD"),
            cancellable = true
    )
    private void drawItemBarMixin(ItemStack stack, int x, int y, CallbackInfo ci) {
        if (stack.isItemBarVisible()) {
            int i = x + 2;
            int j = y + 13;

            this.fill(RenderLayer.getGui(),
                    i, j, i + stack.getItemBarStep(), j + 1,
                    200, ColorHelper.withAlpha(150, stack.getItemBarColor())
            );
            this.fill(RenderLayer.getGui(),
                    i + stack.getItemBarStep(), j, i + 13, j + 1,
                    200, ColorHelper.withAlpha(150, -16777216)
            );
            this.fill(RenderLayer.getGui(),
                    i, j + 1, i + 13, j + 2,
                    200, ColorHelper.withAlpha(150, -16777216)
            );

//            int[][] transparency = {
//                {92, 76, 54},
//                {76, 92, 38},
//                {54, 38, 36}
//            };
//
//            for (int dy = 0; dy < transparency.length; dy++) {
//                for (int dx = 0; dx < transparency[dy].length; dx++) {
//                    this.fill(RenderLayer.getGui(),
//                            x + 1 + dx, y + 1 + dy, x + 2 + dx, y + 2 + dy,
//                            200, ColorHelper.withAlpha(transparency[dy][dx] * 255 / 100, stack.getItemBarColor()));
//                }
//            }
        }

        ci.cancel();
    }
}
