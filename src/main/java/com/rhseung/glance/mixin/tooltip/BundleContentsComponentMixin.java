package com.rhseung.glance.mixin.tooltip;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.component.type.BundleContentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleContentsComponent.class)
public abstract class BundleContentsComponentMixin {
    @Shadow public abstract int size();

    @ModifyReturnValue(method = "getNumberOfStacksShown", at = @At("RETURN"))
    private int getNumberofStacksShownMixin(int original) {
        return this.size();
    }
}
