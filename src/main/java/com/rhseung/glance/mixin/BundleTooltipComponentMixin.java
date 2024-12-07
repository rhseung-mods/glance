package com.rhseung.glance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleTooltipComponent.class)
public class BundleTooltipComponentMixin {
    @ModifyReturnValue(
        method = "getXMargin(I)I",
        at = @At("RETURN")
    )
    private int getXMarginMixin(int width) {
        return 0;
    }
}
