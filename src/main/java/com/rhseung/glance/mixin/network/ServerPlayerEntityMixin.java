package com.rhseung.glance.mixin.network;

import com.rhseung.glance.network.ServerSyncHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    public void tickMixin(CallbackInfo ci) {
        ServerPlayerEntity that = (ServerPlayerEntity) (Object) this;
        ServerSyncHandler.INSTANCE.onPlayerUpdate(that);
    }
}
