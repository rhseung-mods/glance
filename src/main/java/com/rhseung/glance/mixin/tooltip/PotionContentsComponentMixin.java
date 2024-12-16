package com.rhseung.glance.mixin.tooltip;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.type.PotionContentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(PotionContentsComponent.class)
public class PotionContentsComponentMixin {
    @Redirect(
        method = "buildTooltip(Ljava/lang/Iterable;Ljava/util/function/Consumer;FF)V",
        at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z")
    )
    private static boolean isEmptyMixin(List<?> list) {
        // 본문이 !list.isEmpty() 였기 때문에 !를 한 번 앞에 붙여야 기본 긍정 조건문이 됨.
        return !(!list.isEmpty() && Screen.hasShiftDown());
    }
}
