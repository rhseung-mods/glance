package com.rhseung.glance.mixin.tooltip;

import com.llamalad7.mixinextras.sugar.Local;
import com.rhseung.glance.tooltip.Tooltip;
import com.rhseung.glance.tooltip.component.TextComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Optional;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Redirect(
            method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/util/Identifier;)V"
            )
    )
    public void drawTooltipRedirect(DrawContext context, TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int mouseX, int mouseY, Identifier texture, @Local ItemStack stack) {
        List<TooltipComponent> components = new java.util.ArrayList<>(List.of());

        if (!text.isEmpty())
            components.addAll(text.stream().map(TextComponent::new).toList());

        data.ifPresent(tooltipData -> components.add(TooltipComponent.of(tooltipData)));

        Tooltip.INSTANCE.draw(context, textRenderer, HoveredTooltipPositioner.INSTANCE, mouseX, mouseY, components, stack);
    }
}
