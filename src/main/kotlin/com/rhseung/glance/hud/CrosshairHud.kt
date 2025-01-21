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
    val AIM_TEXTURES = (0..4).map { id("hud/crosshair/aim_$it") };
    val AIM_TEXTURE = id("hud/crosshair/aim");

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

        val stack: ItemStack = player.activeItem.takeIf { !it.isEmpty } ?: player.getStackInHand(Hand.MAIN_HAND);

        context.matrices.push();

        if (isAimingItem(stack)) {
            val maxUseTime: Int = when (stack.item) {
                is BowItem -> 20;   // todo: mod support
                is TridentItem -> TridentItem.MIN_DRAW_DURATION;
                is CrossbowItem -> if (CrossbowItem.isCharged(stack)) -1 else stack.getMaxUseTime(player);
                else -> stack.getMaxUseTime(player);
            }
            val useTime: Int = player.itemUseTime;
            val useTimeLeft = maxOf(0, maxUseTime - useTime);

            val offset = 0.7f;
            val ratio: Float = useTimeLeft.toFloat() / maxUseTime * (1f - offset); // 0.3f -> 0f
            val sizeRatio: Float = offset + ratio;   // 1f -> 0.7f

            context.matrices.push();
            context.matrices.scale(sizeRatio, sizeRatio, 1f);
            RenderSystem.enableBlend();
            val x2: Float = x + WIDTH * (1f - sizeRatio) / 2;
            val y2: Float = y + HEIGHT * (1f - sizeRatio) / 2;
            this.draw(context, AIM_TEXTURE, (x2 / sizeRatio).roundToInt(), (y2 / sizeRatio).roundToInt());
            RenderSystem.disableBlend();
            context.matrices.scale(1 / sizeRatio, 1 / sizeRatio, 1f);
            context.matrices.pop();

            if (hitMarkerTicks > 0)
                this.draw(context, HIT_TEXTURE, x, y, isCritical.ifElse(Color.RED, Color.GRAY).toInt(100));
        } else {
            this.draw(context, DEFAULT_TEXTURE, x, y);
            if (client.targetedEntity != null)
                this.draw(context, TARGET_TEXTURE, x, y, Color.GRAY.toInt(100));
            else if (hitMarkerTicks > 0)
                this.draw(context, HIT_TEXTURE, x, y, isCritical.ifElse(Color.RED, Color.GRAY).toInt(100));
        }

        context.matrices.pop();
    }
}