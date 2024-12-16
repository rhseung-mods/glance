package com.rhseung.glance.mixin.tooltip;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OrderedTextTooltipComponent.class)
public class OrderedTextTooltipComponentMixin {
    @ModifyReturnValue(
        method = "getHeight",
        at = @At("RETURN")
    )
    public int getHeightMixin(int original, @Local(argsOnly = true) TextRenderer textRenderer) {
        return textRenderer.fontHeight - 1; // 'g' 를 제외하면 모든 폰트의 높이가 8이라서 9보다 8이 더 예쁘게 보임.
    }
}
