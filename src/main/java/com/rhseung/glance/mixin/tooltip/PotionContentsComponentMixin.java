package com.rhseung.glance.mixin.tooltip;

import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(PotionContentsComponent.class)
public class PotionContentsComponentMixin {
    @Inject(
        method = "buildTooltip(Ljava/lang/Iterable;Ljava/util/function/Consumer;FF)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void buildTooltipMixin(Iterable<StatusEffectInstance> effects, Consumer<Text> textConsumer, float durationMultiplier, float tickRate, CallbackInfo ci) {
        ci.cancel();
    }
}
