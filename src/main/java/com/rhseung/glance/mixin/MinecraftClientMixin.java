package com.rhseung.glance.mixin;

import com.rhseung.glance.event.RenderTickEvents;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            ordinal = 3
        )
    )
    public void renderMixin(boolean tick, CallbackInfo ci) {
        RenderTickEvents.Companion.getEVENT().invoker().tick();
    }
}
