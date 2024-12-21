package com.rhseung.glance.mixin.tooltip;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleTooltipComponent.class)
public abstract class BundleTooltipComponentMixin {
    @ModifyReturnValue(
        method = "getXMargin(I)I",
        at = @At("RETURN")
    )
    private int getXMarginMixin(int width) {
        return 0;
    }

    @ModifyReturnValue(
        method = "getHeightOfEmpty",
        at = @At("RETURN")
    )
    private static int getHeightOfEmptyMixin(int original) {
        return original - 2;    // YPaddingComponent(2) 때문에
    }

    @ModifyReturnValue(
        method = "getHeightOfNonEmpty",
        at = @At("RETURN")
    )
    private int getHeightOfNotEmptyMixin(int original) {
        return original - 2;    // YPaddingComponent(2) 때문에
    }
}
