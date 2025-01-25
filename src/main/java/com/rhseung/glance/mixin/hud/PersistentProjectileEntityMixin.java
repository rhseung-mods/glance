package com.rhseung.glance.mixin.hud;

import com.rhseung.glance.hud.CrosshairHud;
import com.rhseung.glance.hud.crosshair.ProjectileData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {
    @Shadow public abstract boolean isCritical();

    @Shadow protected abstract void onHit(LivingEntity target);

    @Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;onHit(Lnet/minecraft/entity/LivingEntity;)V"))
    private void onEntityHitMixin(PersistentProjectileEntity entity, LivingEntity target) {
        this.onHit(target);

        var player = MinecraftClient.getInstance().player;
        var entityOwner = entity.getOwner();
        if (player != null && entityOwner != null && player.getUuid().equals(entityOwner.getUuid())) {
            CrosshairHud.INSTANCE.onData(new ProjectileData(isCritical()));
        }
    }
}
