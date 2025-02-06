package com.rhseung.glance.mixin.accessor;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
    @Accessor
    GuiAtlasManager getGuiAtlasManager();

    @Accessor
    VertexConsumerProvider.Immediate getVertexConsumers();

    @Invoker("drawSpriteRegion")
    void drawSpriteRegionMixin(
            Function<Identifier, RenderLayer> renderLayers,
            Sprite sprite,
            int textureWidth,
            int textureHeight,
            int u,
            int v,
            int x,
            int y,
            int width,
            int height,
            int color
    );

    @Invoker("drawSpriteStretched")
    void drawSpriteStretchedMixin(
            Function<Identifier, RenderLayer> renderLayers,
            Sprite sprite,
            int x,
            int y,
            int width,
            int height,
            int color
    );
}
