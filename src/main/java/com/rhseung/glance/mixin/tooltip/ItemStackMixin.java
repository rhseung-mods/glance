package com.rhseung.glance.mixin.tooltip;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @WrapWithCondition(
            method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                    ordinal = 1
            )
    )
    public boolean appendTrimTooltipMixin(ItemStack stack, ComponentType<?> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
        return false;
    }

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

    @WrapWithCondition(
            method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                    ordinal = 4
            )
    )
    public boolean appendDyedColorTooltipMixin(ItemStack stack, ComponentType<?> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
        return false;
    }

    @WrapWithCondition(
            method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendAttributeModifiersTooltip(Ljava/util/function/Consumer;Lnet/minecraft/entity/player/PlayerEntity;)V")
    )
    public boolean appendAttributeModifiersTooltipWrap(ItemStack stack, Consumer<Text> textConsumer, PlayerEntity player) {
        return false;
    }

    @WrapWithCondition(
            method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                    ordinal = 7
            )
    )
    public boolean appendOminousBottleAmplifierTooltipMixin(ItemStack stack, ComponentType<?> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
        return false;
    }

    @WrapWithCondition(
            method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                    ordinal = 8
            )
    )
    public boolean appendSuspiciousEffectsTooltipMixin(ItemStack stack, ComponentType<?> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
        return false;
    }
}
