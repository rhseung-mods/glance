package com.rhseung.glance.mixin;

import com.rhseung.glance.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Redirect(
        method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getTooltipData()Ljava/util/Optional;"
        )
    )
    // 툴팁 데이터에 클라이언트 값도 추가
    public Optional<TooltipData> getTooltipDataRedirect(ItemStack stack) {
        return Util.INSTANCE.getTooltipDataWithClient(stack, MinecraftClient.getInstance());
    }
}
