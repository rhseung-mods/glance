package com.rhseung.glance.hud

import com.mojang.blaze3d.systems.RenderSystem
import com.rhseung.glance.ModMain.id
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.ifElse
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.BowItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem
import net.minecraft.item.consume.UseAction
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import kotlin.math.roundToInt

object CrosshairHud {
    val DEFAULT_TEXTURE = id("hud/crosshair/default");
    val TARGET_TEXTURE = id("hud/crosshair/target");
    val HIT_TEXTURE = id("hud/crosshair/hit");
//    val AIM_TEXTURE = id("hud/crosshair/aim");
    val AIM_TOP_TEXTURE = id("hud/crosshair/aim_top");
    val AIM_BOTTOM_TEXTURE = id("hud/crosshair/aim_bottom");
    val AIM_LEFT_TEXTURE = id("hud/crosshair/aim_left");
    val AIM_RIGHT_TEXTURE = id("hud/crosshair/aim_right");

    const val WIDTH = 15;
    const val HEIGHT = 15;

    private var hitMarkerTicks = 0;
    private var isCritical = false;

    fun onTick() {
        if (hitMarkerTicks > 0)
            hitMarkerTicks--;
        else if (hitMarkerTicks == 0)
            isCritical = false;
    }

    fun onArrowHit(critical: Boolean) {
        hitMarkerTicks = 20;
        isCritical = critical;
    }

    private fun draw(context: DrawContext, sprite: Identifier, x: Int, y: Int) {
        context.drawGuiTexture(RenderLayer::getCrosshair, sprite, x, y, WIDTH, HEIGHT);
    }

    private fun draw(context: DrawContext, sprite: Identifier, x: Int, y: Int, color: Int) {
        context.drawGuiTexture(RenderLayer::getCrosshair, sprite, x, y, WIDTH, HEIGHT, color);
    }

    private fun isAimingItem(stack: ItemStack): Boolean {
        return stack.useAction == UseAction.SPEAR || stack.useAction == UseAction.CROSSBOW || stack.useAction == UseAction.BOW;
    }

    fun render(context: DrawContext, x: Int, y: Int) {
        val client: MinecraftClient = MinecraftClient.getInstance();
        val player: ClientPlayerEntity? = client.player;

        if (player == null)
            return;

        val activeStack: ItemStack = player.activeItem.takeIf { !it.isEmpty } ?: player.getStackInHand(Hand.MAIN_HAND);
        val color = isCritical.ifElse(Color.RED, Color.WHITE).toInt(100);

        context.matrices.push();

        this.draw(context, DEFAULT_TEXTURE, x, y);
        if (client.targetedEntity != null)
            this.draw(context, TARGET_TEXTURE, x, y, Color.WHITE.toInt(100));
        if (hitMarkerTicks > 0)
            this.draw(context, HIT_TEXTURE, x, y, color);

        if (isAimingItem(activeStack)) {
            val maxUseTime: Int = when (activeStack.item) {
                is BowItem -> 20;   // todo: mod support
                is TridentItem -> TridentItem.MIN_DRAW_DURATION;
                is CrossbowItem -> if (CrossbowItem.isCharged(activeStack)) -1 else activeStack.getMaxUseTime(player);
                else -> activeStack.getMaxUseTime(player);
            }
            val useTime: Int = player.itemUseTime;
            val ratio: Float = (if (activeStack.item is BowItem) BowItem.getPullProgress(useTime) else useTime.toFloat() / maxUseTime).coerceIn(0f, 1f);
            val diff = (ratio * 5).roundToInt();

            this.draw(context, AIM_TOP_TEXTURE, x, y - diff, Color.WHITE.toInt(100));
            this.draw(context, AIM_BOTTOM_TEXTURE, x, y + diff, Color.WHITE.toInt(100));
            this.draw(context, AIM_LEFT_TEXTURE, x - diff, y, Color.WHITE.toInt(100));
            this.draw(context, AIM_RIGHT_TEXTURE, x + diff, y, Color.WHITE.toInt(100));
        }

        context.matrices.pop();
    }
}