package com.rhseung.glance.mixin.tooltip;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    /**
     * {@link com.rhseung.glance.legacy_tooltip.EnchantedBookTooltip} 에서 대체됨
     */
    @WrapWithCondition(
        method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
            ordinal = 2
        )
    )
    public boolean appendStoredEnchantmentTooltipMixin(ItemStack stack, ComponentType<?> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
        return false;
    }

    // Attribute 툴팁이 Shift를 누르면 원래대로 보이게 하기
    @WrapWithCondition(
        method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendAttributeModifiersTooltip(Ljava/util/function/Consumer;Lnet/minecraft/entity/player/PlayerEntity;)V")
    )
    public boolean appendAttributeModifiersTooltipWrap(ItemStack stack, Consumer<Text> textConsumer, PlayerEntity player) {
        return Screen.hasShiftDown();
    }

    // 내구도를 따로 tooltip component로 따로 출력시키자.
    @Redirect(
        method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamaged()Z")
    )
    public boolean isDamagedRedirect(ItemStack stack) {
        return false;
    }
}
