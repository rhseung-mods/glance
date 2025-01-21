package com.rhseung.glance.mixin;

import com.rhseung.glance.hud.CrosshairHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    @Shadow protected abstract boolean isOwner(Entity entity);

    @Inject(method = "onEntityHit", at = @At("HEAD"))
    private void onEntityHitMixin(EntityHitResult entityHitResult, CallbackInfo ci) {
        ProjectileEntity projectileEntity = (ProjectileEntity) (Object) this;

        if (this.isOwner(MinecraftClient.getInstance().player)) {
            var isCritical = false;
            if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity)
                isCritical = persistentProjectileEntity.isCritical();

            CrosshairHud.INSTANCE.onArrowHit(isCritical);
        }
    }
}
