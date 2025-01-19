package com.rhseung.glance.hud

import com.rhseung.glance.tooltip.icon.HudIcon
import com.rhseung.glance.util.Util.getProperty
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random
import kotlin.math.ceil

object FoodHud {
    private val FOOD_EMPTY_HUNGER_TEXTURE: Identifier = Identifier.ofVanilla("hud/food_empty_hunger");
    private val FOOD_HALF_HUNGER_TEXTURE: Identifier = Identifier.ofVanilla("hud/food_half_hunger");
    private val FOOD_FULL_HUNGER_TEXTURE: Identifier = Identifier.ofVanilla("hud/food_full_hunger");
    private val FOOD_EMPTY_TEXTURE: Identifier = Identifier.ofVanilla("hud/food_empty");
    private val FOOD_HALF_TEXTURE: Identifier = Identifier.ofVanilla("hud/food_half")
    private val FOOD_FULL_TEXTURE: Identifier = Identifier.ofVanilla("hud/food_full");

    fun render(context: DrawContext, player: PlayerEntity, top: Int, right: Int) {
        val textRenderer = MinecraftClient.getInstance().textRenderer;
        val gameHud = MinecraftClient.getInstance().inGameHud;
        val ticks = gameHud.getProperty<Int>("ticks");
        val random = gameHud.getProperty<Random>("random");

        val hungerManager = player.hungerManager;
        val foodLevel = hungerManager.foodLevel;
        val saturationLevel = hungerManager.saturationLevel; // 0f ~ 20f
        val saturationIconCount = ceil(saturationLevel / 2.0).toInt();

        for (j in 0..<10) {
            var y = top;
            val emptyIcon: Identifier;
            val halfIcon: Identifier;
            val fullIcon: Identifier;
            val saturationIcon: HudIcon;

            if (player.hasStatusEffect(StatusEffects.HUNGER)) {
                emptyIcon = FOOD_EMPTY_HUNGER_TEXTURE;
                halfIcon = FOOD_HALF_HUNGER_TEXTURE;
                fullIcon = FOOD_FULL_HUNGER_TEXTURE;
                saturationIcon = HudIcon.SATURATION_HUNGER;
            }
            else {
                emptyIcon = FOOD_EMPTY_TEXTURE;
                halfIcon = FOOD_HALF_TEXTURE;
                fullIcon = FOOD_FULL_TEXTURE;
                saturationIcon = HudIcon.SATURATION;
            }

            if (player.hungerManager.saturationLevel <= 0.0f && ticks % (foodLevel * 3 + 1) == 0) {
                y = top + random.nextInt(3) - 1;
            }

            val x = right - j * 8 - 9;
            context.drawGuiTexture(RenderLayer::getGuiTextured, emptyIcon, x, y, 9, 9);

            if (j * 2 + 1 < foodLevel)
                context.drawGuiTexture(RenderLayer::getGuiTextured, fullIcon, x, y, 9, 9);
            else if (j * 2 + 1 == foodLevel)
                context.drawGuiTexture(RenderLayer::getGuiTextured, halfIcon, x, y, 9, 9);

            if (saturationLevel > 0) {
                if (j < saturationIconCount - 1)
                    saturationIcon.draw(context, textRenderer, x, y, -1);
                else if (j == saturationIconCount - 1)
                    saturationIcon[ceil(((saturationLevel % 2) / 2 * 3).toDouble()).toInt()].draw(context, textRenderer, x, y, -1);
            }
        }
    }
}