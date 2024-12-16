package com.rhseung.glance.mixin.tooltip;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.Settings.class)
public class SettingsMixin {
    // rarity를 이용하여 툴팁 스타일을 변경
    @ModifyReturnValue(
        method = "rarity(Lnet/minecraft/util/Rarity;)Lnet/minecraft/item/Item$Settings;",
        at = @At("RETURN")
    )
    public Item.Settings rarityModifyReturnValue(Item.Settings original, @Local(argsOnly = true, ordinal = 0) Rarity rarity) {
        return original.component(DataComponentTypes.TOOLTIP_STYLE, Identifier.ofVanilla(rarity.name().toLowerCase()));
    }
}
