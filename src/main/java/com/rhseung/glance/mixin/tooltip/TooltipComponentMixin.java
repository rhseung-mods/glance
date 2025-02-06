package com.rhseung.glance.mixin.tooltip;

import com.rhseung.glance.tooltip.component.TextComponent;
import com.rhseung.glance.util.Util;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {
    @Inject(
            method = "of(Lnet/minecraft/text/OrderedText;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void ofMixin(OrderedText text, CallbackInfoReturnable<TooltipComponent> cir) {
        cir.setReturnValue(new TextComponent(Util.INSTANCE.toText(text)));
    }
}
