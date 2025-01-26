package com.rhseung.glance.mixin.tooltip;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.BundleItem;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(BundleItem.class)
public abstract class BundleItemMixin {
    @ModifyReturnValue(method = "getTooltipData", at = @At("RETURN"))
    private Optional<TooltipData> getTooltipDataMixin(Optional<TooltipData> original) {
        return Optional.empty();
    }
}
