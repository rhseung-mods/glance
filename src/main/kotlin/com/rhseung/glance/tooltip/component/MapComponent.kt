package com.rhseung.glance.tooltip.component

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.MapRenderState
import net.minecraft.client.render.RenderLayer
import net.minecraft.component.type.MapIdComponent
import net.minecraft.item.FilledMapItem
import net.minecraft.util.Identifier

class MapComponent(val mapId: MapIdComponent) : GlanceTooltipComponent {
    val background = Identifier.ofVanilla("textures/map/map_background.png");
    val client = MinecraftClient.getInstance();
    val mapRenderer = client.mapRenderer;

    override fun getWidth(textRenderer: TextRenderer): Int {
        return 64;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return 64;
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int
    ) {
        val mapState = FilledMapItem.getMapState(mapId, client.world);
        val mapRenderState = MapRenderState();

        context.matrices.push();
        context.drawTexture(RenderLayer::getGuiTextured, background, x0, y0, 0f, 0f, 64, 64, 64, 64);
        context.matrices.pop();

        context.matrices.push();
        context.matrices.translate(x0 + 3.2f, y0 + 3.2f, 401f);
        context.matrices.scale(0.45f, 0.45f, 1f);
        mapRenderer.update(mapId, mapState, mapRenderState);
        context.draw { mapRenderer.draw(mapRenderState, context.matrices, it, true, 15728880) };
        context.matrices.pop();
    }
}