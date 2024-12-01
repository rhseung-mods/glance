package com.rhseung.glance.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @WrapWithCondition(
        method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendAttributeModifiersTooltip(Ljava/util/function/Consumer;Lnet/minecraft/entity/player/PlayerEntity;)V")
    )
    // Attribute 툴팁이 Shift를 누르면 원래대로 보이게 하기
    public boolean appendAttributeModifiersTooltipWrap(ItemStack stack, Consumer<Text> textConsumer, PlayerEntity player) {
        return Screen.hasShiftDown();
    }

    @Redirect(
        method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamaged()Z")
    )
    // 내구도를 따로 tooltip component로 따로 출력시키자.
    public boolean isDamagedRedirect(ItemStack stack) {
        return false;
    }
}
