//package com.rhseung.glance.mixin;
//
//import com.llamalad7.mixinextras.sugar.Local;
//import com.rhseung.glance.tooltip.Draw;
//import com.rhseung.glance.util.Color;
//import net.minecraft.client.font.TextRenderer;
//import net.minecraft.client.gui.DrawContext;
//import net.minecraft.client.gui.tooltip.TooltipComponent;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.Rarity;
//import org.jetbrains.annotations.Nullable;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//
//import java.util.List;
//
//@Mixin(DrawContext.class)
//public class DrawContextMixin {
//    @Redirect(
//        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V",
//        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawItems(Lnet/minecraft/client/font/TextRenderer;IIIILnet/minecraft/client/gui/DrawContext;)V")
//    )
//    public void drawTitleAndSeparator(
//        TooltipComponent component,
//        TextRenderer textRenderer,
//        int x,
//        int y,
//        int width,
//        int height,
//        DrawContext context,
////        @Local(ordinal = 9) int q,
//        @Local(ordinal = 10) int r,
//        @Local(ordinal = 0, argsOnly = true) @Nullable Identifier texture,
//        @Local(ordinal = 0, argsOnly = true) List<TooltipComponent> components
//    ) {
//        var id = texture != null ? texture.getPath() : "common";
//        var color = Rarity.valueOf(id.toUpperCase()).getFormatting().getColorValue();
//
//        component.drawItems(textRenderer, x, y, width, height, context);
//        if (components.size() > 1 && r == 0) {
//            // draw separator
////            Draw.INSTANCE.draw("abc", context, textRenderer, x, y, new Color(color));
//            context.fill(x, y + component.getHeight(textRenderer), x + width, y + component.getHeight(textRenderer) + height, color);
////            context.drawHorizontalLine(x, x + component.getWidth(textRenderer), y + component.getHeight(textRenderer), color);
//        }
//    }
//}
