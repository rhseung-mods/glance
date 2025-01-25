package com.rhseung.glance.mixin.hud;

import com.llamalad7.mixinextras.sugar.Local;
import com.rhseung.glance.hud.CrosshairHud;
import com.rhseung.glance.hud.crosshair.AttackData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Redirect(
        method = "attack",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/Entity;sidedDamage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
        )
    )
    private boolean isCritical(Entity target, DamageSource source, float amount, @Local(ordinal = 2) boolean isCritical) {
        CrosshairHud.INSTANCE.onData(new AttackData(amount, isCritical));
        return target.sidedDamage(source, amount);
    }
}
