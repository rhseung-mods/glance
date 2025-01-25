package com.rhseung.glance.hud

import com.rhseung.glance.ModMain.id
import com.rhseung.glance.hud.crosshair.AttackData
import com.rhseung.glance.hud.crosshair.CrosshairData
import com.rhseung.glance.hud.crosshair.ProjectileData
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.drawGuiTextureFloat
import com.rhseung.glance.util.Util.ifElse
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.AttackIndicator
import net.minecraft.client.render.RenderLayer
import net.minecraft.entity.LivingEntity
import net.minecraft.item.BowItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.MiningToolItem
import net.minecraft.item.TridentItem
import net.minecraft.item.consume.UseAction
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.RotationAxis
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.reflect.KClass

object CrosshairHud {
    val VANILLA_TEXTURE = Identifier.ofVanilla("hud/crosshair");
    val CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_full");
    val CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_background");
    val CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_progress");
    val HIT_TEXTURE = id("hud/crosshair/hit");
    val AIM_TOP_TEXTURE = id("hud/crosshair/aim_top");
    val AIM_BOTTOM_TEXTURE = id("hud/crosshair/aim_bottom");
    val AIM_LEFT_TEXTURE = id("hud/crosshair/aim_left");
    val AIM_RIGHT_TEXTURE = id("hud/crosshair/aim_right");

    const val WIDTH = 15;
    const val HEIGHT = 15;

    private var crosshairDatas = mutableMapOf<KClass<out CrosshairData>, CrosshairData>();
    var angle = 0f;
    var scale = 1f;

    fun init() {
        angle = 0f;
        scale = 1f;
    }

    fun onTick() {
        crosshairDatas = crosshairDatas
            .onEach { (_, data) -> data.onTick() }
            .filterValues { !it.isFinished() }
            .toMutableMap();
    }

    fun onData(data: CrosshairData) {
        init();
        crosshairDatas[data::class] = data;
    }

    private fun draw(context: DrawContext, sprite: Identifier, x: Float, y: Float, color: Int = -1) {
        context.drawGuiTextureFloat(RenderLayer::getCrosshair, sprite, x, y, WIDTH.toFloat(), HEIGHT.toFloat(), color);
    }

    private fun draw(context: DrawContext, sprite: Identifier, x: Float, y: Float, u: Float, v: Float, width: Float, height: Float, color: Int = -1) {
        context.drawGuiTextureFloat(RenderLayer::getCrosshair, sprite, WIDTH.toFloat(), HEIGHT.toFloat(), u, v, x, y, width, height, color);
    }

    private fun isAimingItem(stack: ItemStack): Boolean {
        return stack.useAction == UseAction.SPEAR || stack.useAction == UseAction.CROSSBOW || stack.useAction == UseAction.BOW;
    }

    fun render(context: DrawContext, centerX: Float, centerY: Float) {
        val x1: Float = ((centerX - WIDTH.toFloat() / 2) * 4).roundToInt() / 4f;
        val y1: Float = ((centerY - HEIGHT.toFloat() / 2) * 4).roundToInt() / 4f;
        val client: MinecraftClient = MinecraftClient.getInstance();
        val player: ClientPlayerEntity = client.player ?: return;
        val activeStack: ItemStack = player.activeItem.takeIf { !it.isEmpty } ?: player.getStackInHand(Hand.MAIN_HAND);

        val projectileData = crosshairDatas[ProjectileData::class] as ProjectileData?;
        val attackData = crosshairDatas[AttackData::class] as AttackData?;

        ///////// set angle, scale /////////
        if (projectileData != null) {
            // nothing
        }
        else if (attackData != null) {
            val tickRatio: Float = attackData.tick.toFloat() / attackData.maxTicks;
            val start = (0.5 * PI).toFloat();
            val end = (1.5 * PI).toFloat();
            val now = start + (end - start) * tickRatio;

            angle += sin(now) * attackData.attackDamage;
            scale *= 1f + if (attackData.isCritical)
                sin(now) * 0.1f;
            else
                sin(now) * 0.03f;
        }
        else {
            init();
        }

        context.matrices.push();
        context.matrices.translate(centerX, centerY, 0f);
        context.matrices.multiply(RotationAxis.of(Vector3f(0f, 0f, 1f)).rotationDegrees(angle));
        context.matrices.scale(scale, scale, 1f);
        val x1_translate = x1 - centerX;
        val y1_translate = y1 - centerY;

        client.crosshairTarget?.type == HitResult.Type.BLOCK

        /////////// render main crosshair ///////////
        val mainCrosshairColor = when {
            attackData?.isCritical == true
                 -> Color(0xff5555)
            activeStack.item is MiningToolItem &&
            client.crosshairTarget?.type == HitResult.Type.BLOCK &&
            client.world != null &&
            activeStack.isSuitableFor(client.world!!.getBlockState((client.crosshairTarget as BlockHitResult).blockPos))
                -> Color(0xffffff)
            activeStack.item !is MiningToolItem && client.targetedEntity != null
                 -> Color(0xffffff)
            projectileData != null || attackData != null
                 -> Color(0xffffff)
            else
                 -> Color(0x858585)
        }.toInt();

        if (isAimingItem(activeStack)) {
            val useTime: Int = player.itemUseTime;

            val maxUseTime: Int = when (activeStack.item) {
                is BowItem -> 20;   // todo: mod support
                is TridentItem -> TridentItem.MIN_DRAW_DURATION;
                is CrossbowItem -> activeStack.getMaxUseTime(player);
                else -> activeStack.getMaxUseTime(player);
            };

            val useRatio: Float = when {
                activeStack.item is BowItem -> BowItem.getPullProgress(useTime);
                CrossbowItem.isCharged(activeStack) -> 1f
                else -> useTime.toFloat() / maxUseTime;
            }.coerceIn(0f, 1f);

            val diff: Float = (1f - useRatio) * 4 - 1f;    // 3f -> -1f

            // top
            this.draw(context, VANILLA_TEXTURE, x1_translate + 7f, y1_translate - diff + 3f, 7f, 3f, 1f, 4f, mainCrosshairColor);
            // bottom
            this.draw(context, VANILLA_TEXTURE, x1_translate + 7f, y1_translate + diff + 8f, 7f, 8f, 1f, 4f, mainCrosshairColor);
            // left
            this.draw(context, VANILLA_TEXTURE, x1_translate - diff + 3f, y1_translate + 7f, 3f, 7f, 4f, 1f, mainCrosshairColor);
            // right
            this.draw(context, VANILLA_TEXTURE, x1_translate + diff + 8f, y1_translate + 7f, 8f, 7f, 4f, 1f, mainCrosshairColor);
        }
        else {
            this.draw(context, VANILLA_TEXTURE, x1_translate, y1_translate, mainCrosshairColor);
        }

        /////////// render hover crosshair ///////////
        if (projectileData != null) {
            this.draw(
                context,
                HIT_TEXTURE,
                x1_translate,
                y1_translate,
                projectileData.isCritical.ifElse(Color(0xff5555), Color(0x858585)).toInt()
            );
        }

        ////////// render attack indicator //////////
        if (client.options.attackIndicator.value == AttackIndicator.CROSSHAIR) {
            val cooldownProgress = player.getAttackCooldownProgress(0f);
            var bl = false;
            if (client.targetedEntity != null && client.targetedEntity is LivingEntity && cooldownProgress >= 1f) {
                bl = player.attackCooldownProgressPerTick > 5f;
                bl = bl and (client.targetedEntity as LivingEntity).isAlive;
            }

            val x2 = context.scaledWindowWidth.toFloat() / 2 - 8;
            val y2 = context.scaledWindowHeight.toFloat() / 2 + 9;
            val x2_translate = x2 - centerX;
            val y2_translate = y2 - centerY;
            if (bl) {
                context.drawGuiTextureFloat(RenderLayer::getCrosshair, CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, x2_translate, y2_translate, 16f, 16f);
            } else if (cooldownProgress < 1f) {
                val length = (cooldownProgress * 17f).toInt();
                context.drawGuiTextureFloat(RenderLayer::getCrosshair, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, x2_translate, y2_translate, 16f, 4f);
                context.drawGuiTextureFloat(RenderLayer::getCrosshair, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16f, 4f, 0f, 0f, x2_translate, y2_translate, length.toFloat(), 4f);
            }
        }

        context.matrices.pop();
    }
}