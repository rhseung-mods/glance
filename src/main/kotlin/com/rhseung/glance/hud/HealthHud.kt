package com.rhseung.glance.hud

import com.rhseung.glance.tooltip.icon.HudIcon
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Util.getProperty
import com.rhseung.glance.util.Util.toInt
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.random.Random
import kotlin.math.ceil

object HealthHud {
    private fun shine(context: DrawContext, heartX: Int, heartY: Int) {
        context.fill(heartX + 2, heartY + 2, heartX + 3, heartY + 3, Color.WHITE.toInt(200));
    }

    private fun drawHeart(
        context: DrawContext,
        textRenderer: TextRenderer,
        player: PlayerEntity,
        blinking: Boolean,
        accHealth: Int,
        targetHealth: Int,
        x: Int,
        y: Int,
        color: Color
    ) {
        val isHardcore = player.world.levelProperties.isHardcore;
        val heartIcon = HudIcon.fromPlayerState(player, blinking, isHardcore);
        val isHalf = (accHealth + 1 == targetHealth).toInt();

        if (heartIcon == null) {
            HudIcon.HEART[isHalf].draw(context, textRenderer, x, y, color.toInt());

            if (!isHardcore)
                shine(context, x, y);
            else
                HudIcon.HEART_HARDCORE_SIGN[isHalf].draw(context, textRenderer, x, y);
        }
        else {
            heartIcon[isHalf].draw(context, textRenderer, x, y);
        }
    }

    fun render(
        context: DrawContext,
        player: PlayerEntity,
        x0: Int,
        y0: Int,
        lineMargin: Int,
        regeneratingHeartIndex: Int,
        maxHealth: Float,
        toHealth: Int,
        fromHealth: Int,
        absorption: Int,
        blinking: Boolean
    ) {
        val gameHud = MinecraftClient.getInstance().inGameHud;
        val random = gameHud.getProperty<Random>("random");
        val textRenderer = gameHud.textRenderer;

        val maxHealth = maxHealth + absorption;
        val toHealth = toHealth + absorption;
        val fromHealth = fromHealth + absorption;

        val currentHeartCount = ceil(fromHealth / 2.0).toInt();
        val futureHeartCount = ceil(toHealth / 2.0).toInt();

        val colors = listOf(0xFF1313, 0xff8b20, 0xFFC747, 0x00FF00, 0x2C9EF0, 0xB15AFF).map(::Color);
        val blinkingColors = listOf(0xFF898B, 0xFFAF7D, 0xFFDB8C, 0x9EFF97, 0x99C3F0, 0xCD9BFF).map(::Color);

        val maxHeartCount = maxOf(currentHeartCount, futureHeartCount).coerceAtLeast(10);
        val minHeartCount = (minOf(currentHeartCount, futureHeartCount) - 11).coerceAtLeast(0);

        val yList = List<Int?>(10) { null }.toMutableList();

        // maxHeartCount가 11개면 x2, 21개면 x3
        val stackedLine = (futureHeartCount - 1) / 10 + 1;

        if (stackedLine > 1) {
//            val text = "${stackedLine}x";
//            val width = FontIcon.TINY_NUMBERS.getWidth(text);
//            FontIcon.TINY_NUMBERS.draw(context, textRenderer, x0 - width - 1, y0 + 1, text);

            for (i in stackedLine - 1 downTo 1) {
                HudIcon.HEART_CONTAINER[0].draw(context, textRenderer, x0 - 7 - i * 3, y0);
                drawHeart(context, textRenderer, player, false, 0, 100, x0 - 7 - i * 3, y0, colors[(stackedLine - 1 - i) % colors.size]);
            }
        }

        for (i in minHeartCount..<maxHeartCount) {
            val row = i / 10;
            val col = i % 10;
            val x = x0 + col * 8;
            var y = y0;
            val accHealth = i * 2;

            val color = colors[row % colors.size];
            val blinkingColor = blinkingColors[row % blinkingColors.size];

            // 체력 부족하면 덜덜 떨리는 효과
            if (toHealth + absorption <= 4)
                y += random.nextInt(2);

            // 재생 효과 시 하트 물타기하는 효과
            if (i in minHeartCount..<maxHeartCount.coerceAtMost(minHeartCount + 10) && i == regeneratingHeartIndex)
                y -= 2;

            // 뒤에 겹쳐진 하트도 y좌표 동기화
            if (yList[i % 10] == null)
                yList[i % 10] = y;
            y = yList[i % 10]!!;

            // container
            if (i in minHeartCount..<maxHeartCount.coerceAtMost(minHeartCount + 10))
                HudIcon.HEART_CONTAINER[blinking.toInt()].draw(context, textRenderer, x, y);

            // blinking heart
            if (blinking && accHealth < fromHealth)
                drawHeart(context, textRenderer, player, true, accHealth, fromHealth, x, y, blinkingColor);

            // default heart
            if (accHealth < toHealth)
                drawHeart(context, textRenderer, player, false, accHealth, toHealth, x, y, color);
        }
    }
}