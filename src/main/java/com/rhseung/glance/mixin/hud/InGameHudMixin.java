package com.rhseung.glance.mixin.hud;

import com.rhseung.glance.hud.ArmorHud;
import com.rhseung.glance.hud.FoodHud;
import com.rhseung.glance.hud.HealthHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    private static void renderArmor(DrawContext context, PlayerEntity player, int i, int j, int k, int x) {
    }

    @Shadow protected abstract void renderHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking);

    @Shadow private int ticks;

    @Redirect(
        method = "renderStatusBars",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderArmor(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIII)V"
        )
    )
    private void renderStatusBarsArmorRedirect(DrawContext context, PlayerEntity player, int n, int p, int q, int k) {
        float absorptionAmount = player.getAbsorptionAmount();

        // 흡수와 체력 부스트 효과가 아무리 커도 무조건 1줄만 차지하도록 수정 (겹쳐지게 할거니까)
        p = 1;   // absorptionAmount > 0 ? 2 : 1;
        q = Math.max(10 - (p - 2), 3);

        renderArmor(context, player, n, p, q, k);
    }

    @Redirect(
        method = "renderStatusBars",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"
        )
    )
    private void renderStatusBarsHealthRedirect(InGameHud instance, DrawContext context, PlayerEntity player, int x0, int y0, int lineMargin, int regeneratingHeartIndex, float maxHealth, int toHealth, int fromHealth, int absorption, boolean blinking) {
        int currentHeartCount = (int) Math.ceil(fromHealth / 2.0);
        int futureHeartCount = (int) Math.ceil(toHealth / 2.0);

        int minHeartCount = Math.max(Math.min(currentHeartCount, futureHeartCount) - 11, 0);
        int maxHeartCount = Math.max(Math.min(currentHeartCount, futureHeartCount), 10);

        int rangeStart = minHeartCount;
        int rangeEndExclusive = Math.min(maxHeartCount, minHeartCount + 10);

        int regeneratingHeartIndexModified = -1;
        if (player.hasStatusEffect(StatusEffects.REGENERATION)) {
            regeneratingHeartIndexModified = this.ticks % (rangeEndExclusive - rangeStart) + rangeStart;
        }

        renderHealthBar(context, player, x0, y0, lineMargin, regeneratingHeartIndexModified, maxHealth, toHealth, fromHealth, absorption, blinking);
    }

    @Inject(
        method = "renderArmor",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void renderArmorMixin(DrawContext context, PlayerEntity player, int y0, int lines, int q, int x0, CallbackInfo ci) {
        ArmorHud.INSTANCE.render(context, player, x0, y0 - (lines - 1) * q - 10);
        ci.cancel();
    }

    @Inject(
        method = "renderFood",
        at = @At("HEAD"),
        cancellable = true
    )
    private void renderFoodHeadMixin(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        FoodHud.INSTANCE.render(context, player, top, right);
        ci.cancel();
    }

    @Inject(
        method = "renderHealthBar",
        at = @At("HEAD"),
        cancellable = true
    )
    private void renderHealthBarMixin(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        HealthHud.INSTANCE.render(context, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
        ci.cancel();
    }
}
