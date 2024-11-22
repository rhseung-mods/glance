package com.rhseung.glance.mixin;

import com.rhseung.glance.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;
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
    public Optional<TooltipData> getTooltipDataRedirect(ItemStack stack) {
        var that = (HandledScreen<?>) (Object) this;

        Class<?> clazz = that.getClass();
        while (clazz != Screen.class)
            clazz = clazz.getSuperclass();

        Field field = null;
        try {
            field = clazz.getDeclaredField("client");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("HandledScreenMixin: There is no field 'client' in HandledScreen");
        }
        field.setAccessible(true);

        MinecraftClient client = null;
        try {
            client = (MinecraftClient) field.get(that);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("HandledScreenMixin: Failed to get field 'client' from HandledScreen");
        }

        return Util.INSTANCE.getTooltipDataWithClient(stack, client);
    }
}
