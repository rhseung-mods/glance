package com.rhseung.glance.mixin;

import com.rhseung.glance.tooltip.util.TooltipUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Optional;

@Mixin(Screen.class)
public class ScreenMixin {
    @Redirect(
        method = "renderWithTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Lnet/minecraft/client/gui/tooltip/TooltipPositioner;II)V"
        )
    )
    public final void renderWithTooltipMixin(DrawContext context, TextRenderer textRenderer, List<OrderedText> text, TooltipPositioner positioner, int mouseX, int mouseY) {
        TooltipUtil.INSTANCE
                .drawTooltipOrderedText(context, textRenderer, text, Optional.empty(), mouseX, mouseY, positioner, null);
    }
}
