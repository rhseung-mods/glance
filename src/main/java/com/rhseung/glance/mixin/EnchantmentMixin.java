package com.rhseung.glance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    // 보물 인챈트는 보라색으로 표시, 저주 인챈트는 빨간색으로 표시 (이건 바닐라에서 지원)
    @ModifyReturnValue(
        method = "getName(Lnet/minecraft/registry/entry/RegistryEntry;I)Lnet/minecraft/text/Text;",
        at = @At("RETURN")
    )
    private static Text TreasureEnchantmentPurple(Text original, @Local(argsOnly = true, ordinal = 0) RegistryEntry<Enchantment> enchantment, @Local(ordinal = 0) MutableText mutableText) {
        if (enchantment.isIn(EnchantmentTags.TREASURE) && !enchantment.isIn(EnchantmentTags.CURSE))
            mutableText.setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE));

        return mutableText;
    }
}
