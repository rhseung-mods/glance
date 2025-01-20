package com.rhseung.glance.hud

import com.rhseung.glance.tooltip.icon.HudIcon
import com.rhseung.glance.tooltip.icon.HudIcon.Companion.ARMOR_LEATHER
import com.rhseung.glance.tooltip.icon.HudIcon.Companion.equipmentModelToIcon
import com.rhseung.glance.util.Util.forEachRight
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.DyedColorComponent
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.ColorHelper
import kotlin.math.floor

object ArmorHud {
    private val ARMOR_EMPTY_TEXTURE: Identifier = Identifier.ofVanilla("hud/armor_empty");

    data class ArmorData(val slot: EquipmentSlot, val stack: ItemStack, var armorPoint: Int, val hudIcon: HudIcon);

    private fun getEquipmentSlot(player: PlayerEntity, stack: ItemStack): EquipmentSlot? {
        return EquipmentSlot.VALUES.find { slot -> player.getEquippedStack(slot) == stack };
    }

    private fun getArmorData(player: PlayerEntity): List<ArmorData> {
        val armors = mutableListOf<ArmorData>();

        player.armorItems.forEach { stack ->
            if (!stack.isEmpty) {
                val slot = getEquipmentSlot(player, stack) ?: return@forEach;
                val equipComponent = stack.get(DataComponentTypes.EQUIPPABLE) ?: return@forEach;
                val attributeModifiersComponent = stack.getOrDefault(
                    DataComponentTypes.ATTRIBUTE_MODIFIERS,
                    AttributeModifiersComponent.DEFAULT
                );

                val armorPoint = attributeModifiersComponent.modifiers()
                    .filter { entry -> entry.slot().matches(slot) && entry.attribute() == EntityAttributes.ARMOR }
                    .map { entry -> entry.modifier().value().toInt() }
                    .getOrElse(0) { 0 };

                if (armorPoint > 0) {
                    equipComponent.model().ifPresent { id ->
                        // addFirst를 해야 helment - chestplate - leggings - boots 순서임
                        armors.addFirst(ArmorData(slot, stack, armorPoint, equipmentModelToIcon(id)));
                    }
                }
            }
        }

        return armors;
    }

    /**
     * [ItemColors] 참고
     */
    private fun getColor(hudIcon: HudIcon, stack: ItemStack): Int {
        var color = -1;

        if (hudIcon.id === ARMOR_LEATHER.id) {
            val dyedColorComponent = stack.get(DataComponentTypes.DYED_COLOR);

            color = if (dyedColorComponent != null)
                ColorHelper.fullAlpha(dyedColorComponent.rgb);
            else
                DyedColorComponent.DEFAULT_COLOR;
        }

        return color;
    }

    fun render(context: DrawContext, player: PlayerEntity, x0: Int, y0: Int) {
        val textRenderer = MinecraftClient.getInstance().textRenderer;

        var armors = getArmorData(player);
        var accPoint = 0;
        var eachPoint = 0;

        val linedIndices = mutableSetOf<Int>();
        val totalPoint = armors.sumOf { it.armorPoint };

        if (totalPoint <= 0)
            return;

        // 21 -> 1, 20 -> 0, 40 -> 1, 41 -> 2
        val stackedLine = (totalPoint - 1) / 20;
        var remain = totalPoint - stackedLine * 20;

        // 26 -> stackedLine: 1, remain: 6
        armors.forEachRight { armor ->
            if (remain <= 0)
               armor.armorPoint = 0;
            else if (armor.armorPoint > remain) {
                armor.armorPoint = remain;
                remain = 0;
            }
            else {
                remain -= armor.armorPoint;
            }
        };

        armors = armors.filter { it.armorPoint > 0 };

        for (i in 0..<10) {
            val x = x0 + i * 8;
            context.drawGuiTexture(RenderLayer::getGuiTextured, ARMOR_EMPTY_TEXTURE, x, y0, 9, 9);
        }

        for (i in armors.indices) {
            val armor = armors[i];
            val slot = armor.slot;
            val stack = armor.stack;
            val armorPoint = armor.armorPoint;
            val hudIcon = armor.hudIcon;

            val color = getColor(hudIcon, stack);

            while (eachPoint < armorPoint) {
                val n = floor(accPoint / 2.0).toInt();
                val x = x0 + n * 8;

                var icon: HudIcon;

                // 오른쪽 반
                if (accPoint % 2 == 1) {
                    icon = hudIcon[2];
                    accPoint++;
                    eachPoint++;
                    linedIndices.add(n);
                }
                // 왼쪽 반
                else if (eachPoint == armorPoint - 1) {
                    // 다음 바와 재질이 동일하면 이어서 그려지도록
                    if (i < armors.size - 1 && hudIcon.id === armors[i + 1].hudIcon.id && color == getColor(armors[i + 1].hudIcon, armors[i + 1].stack)) {
                        icon = hudIcon[0];
                        accPoint += 2;
                        eachPoint += 2;
                    }
                    else {
                        icon = hudIcon[1];
                        accPoint++;
                        eachPoint++;
                    }
                }
                // 전체
                else {
                    icon = hudIcon[0];
                    accPoint += 2;
                    eachPoint += 2;
                }

                icon.draw(context, textRenderer, x, y0, color);
            }

            eachPoint -= armorPoint; // 0 or 1 이 남도록
        }

        if (stackedLine > 0) {
            for (i in stackedLine downTo 1)
                HudIcon.ARMOR_DEFAULT.draw(context, textRenderer, x0 - 7 - i * 3, y0);
        }

//        val toughness = player.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS).toInt(); // 원래 값은 0.1인데 * 10 안해도 풀세트가 4로 나오네?
//        val toughtnessIconCount = ceil(toughness / 2.0).toInt();
//
//        if (toughness > 0) {
//            for (n in 0..<toughtnessIconCount) {
//                val x = x0 + n * 8;
//
//                if (n == toughtnessIconCount - 1)
//                    TOUGHNESS[toughness % 2 + if (n in linedIndices) 2 else 0].draw(context, textRenderer, x, y0, -1);
//                else
//                    TOUGHNESS[if (n in linedIndices) 2 else 0].draw(context, textRenderer, x, y0, -1);
//            }
//        }
    }
}