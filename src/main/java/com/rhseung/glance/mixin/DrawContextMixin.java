package com.rhseung.glance.mixin;

import com.rhseung.glance.tooltip.util.TooltipUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Inject(
        method = "drawItemTooltip",
        at = @At("HEAD"),
        cancellable = true
    )
    public void drawItemTooltipMixin(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        DrawContext context = (DrawContext) (Object) this;

        TooltipUtil.INSTANCE.drawTooltipText(
            context, textRenderer,
            Screen.getTooltipFromItem(MinecraftClient.getInstance(), stack),
            stack.getTooltipData(),
            x, y,
            HoveredTooltipPositioner.INSTANCE,
            stack
        );

        ci.cancel();
    }

    @Inject(
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/util/Identifier;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void drawTooltipMixin(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, Identifier texture, CallbackInfo ci) {
        DrawContext context = (DrawContext) (Object) this;

        TooltipUtil.INSTANCE.drawTooltipText(
            context, textRenderer, text, data, x, y, HoveredTooltipPositioner.INSTANCE, null
        );

        ci.cancel();
    }

    @Inject(
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void drawTooltipMixin(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, Identifier texture, CallbackInfo ci) {
        DrawContext context = (DrawContext) (Object) this;

        TooltipUtil.INSTANCE.drawTooltip(
            context, textRenderer, components, x, y, positioner, null
        );

        ci.cancel();
    }
}
