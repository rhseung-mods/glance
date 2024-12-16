package com.rhseung.glance.draw.element.icon

import com.rhseung.glance.ModMain
import com.rhseung.glance.draw.DrawHelper
import com.rhseung.glance.draw.element.Icon
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.equipment.EquipmentModels
import net.minecraft.util.Identifier

class HudIcon(
    private val name: String,
    variants: Int = 1,
    index: Int = 0,
    override val id: Identifier = ModMain.id("hud/$name")
) : Icon(variants, 9, 9, index) {

    constructor(id: Identifier, variants: Int = 1, index: Int = 0) : this(id.path, variants, index, id);

    override fun get(index: Int): HudIcon {
        if (index < 0 || index >= variants)
            throw Error("index=$index is not valid");

        return HudIcon(name, variants, index);
    }

    override fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int): Int {
        context.drawGuiTexture(RenderLayer::getGuiTextured, id, width * variants, height, width * index, 0, x0, y0, width, height);
        return x0 + width;
    }

    fun draw(context: DrawContext, renderer: TextRenderer, x0: Int, y0: Int, color: Int): Int {
        DrawHelper.drawGuiTextureColor(context, RenderLayer::getGuiTextured, id, width * variants, height, width * index, 0, x0, y0, width, height, color);
        return x0 + width;
    }

    companion object {
        val TOUGHNESS = HudIcon("toughness", 4);

        val SATURATION = HudIcon("saturation", 4);
        val SATURATION_HUNGER = HudIcon("saturation_hunger", 4);

        val HEART = HudIcon("heart", 2);
        val HEART_CONTAINER = HudIcon("heart_container", 2);
        val HEART_HARDCORE_SIGN = HudIcon("heart_hardcore_sign", 2);
        val HEART_POISONED = listOf("hud/heart/poisoned_full", "hud/heart/poisoned_half").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_POISONED_BLINKING = listOf("hud/heart/poisoned_full_blinking", "hud/heart/poisoned_half_blinking").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_POISONED_HARDCORE = listOf("hud/heart/poisoned_hardcore_full", "hud/heart/poisoned_hardcore_half").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_POISONED_HARDCORE_BLINKING = listOf("hud/heart/poisoned_hardcore_full_blinking", "hud/heart/poisoned_hardcore_half_blinking").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_WITHERED = listOf("hud/heart/withered_full", "hud/heart/withered_half").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_WITHERED_BLINKING = listOf("hud/heart/withered_full_blinking", "hud/heart/withered_half_blinking").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_WITHERED_HARDCORE = listOf("hud/heart/withered_hardcore_full", "hud/heart/withered_hardcore_half").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_WITHERED_HARDCORE_BLINKING = listOf("hud/heart/withered_hardcore_full_blinking", "hud/heart/withered_hardcore_half_blinking").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_FROZEN = listOf("hud/heart/frozen_full", "hud/heart/frozen_half").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_FROZEN_BLINKING = listOf("hud/heart/frozen_full_blinking", "hud/heart/frozen_half_blinking").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_FROZEN_HARDCORE = listOf("hud/heart/frozen_hardcore_full", "hud/heart/frozen_hardcore_half").map(Identifier::ofVanilla).map(::HudIcon);
        val HEART_FROZEN_HARDCORE_BLINKING = listOf("hud/heart/frozen_hardcore_full_blinking", "hud/heart/frozen_hardcore_half_blinking").map(Identifier::ofVanilla).map(::HudIcon);

        val ARMOR_NETHERITE = HudIcon("armor/netherite", 3);
        val ARMOR_DIAMOND = HudIcon("armor/diamond", 3);
        val ARMOR_IRON = HudIcon("armor/iron", 3);
        val ARMOR_GOLD = HudIcon("armor/gold", 3);
        val ARMOR_LEATHER = HudIcon("armor/leather", 3);
        val ARMOR_CHAINMAIL = HudIcon("armor/chainmail", 3);
        val ARMOR_TURTLE_SCUTE = HudIcon("armor/turtle_scute", 3);
        val ARMOR_DEFAULT = HudIcon("armor/default", 3);

        fun equipmentModelToIcon(id: Identifier) = when (id) {
            EquipmentModels.NETHERITE -> ARMOR_NETHERITE
            EquipmentModels.DIAMOND -> ARMOR_DIAMOND
            EquipmentModels.IRON -> ARMOR_IRON
            EquipmentModels.GOLD -> ARMOR_GOLD
            EquipmentModels.LEATHER -> ARMOR_LEATHER
            EquipmentModels.CHAINMAIL -> ARMOR_CHAINMAIL
            EquipmentModels.TURTLE_SCUTE -> ARMOR_TURTLE_SCUTE
            else -> ARMOR_DEFAULT
        }

        /**
         * [net.minecraft.client.gui.hud.InGameHud.HeartType.fromPlayerState]
         */
        fun fromPlayerState(player: PlayerEntity, blinking: Boolean, hardcore: Boolean): List<HudIcon>? {
            return if (player.hasStatusEffect(StatusEffects.POISON))
                if (blinking && hardcore)
                    HEART_POISONED_HARDCORE_BLINKING
                else if (blinking)
                    HEART_POISONED_BLINKING
                else if (hardcore)
                    HEART_POISONED_HARDCORE
                else
                    HEART_POISONED
            else if (player.hasStatusEffect(StatusEffects.WITHER))
                if (blinking && hardcore)
                    HEART_WITHERED_HARDCORE_BLINKING
                else if (blinking)
                    HEART_WITHERED_BLINKING
                else if (hardcore)
                    HEART_WITHERED_HARDCORE
                else
                    HEART_WITHERED
            else if (player.isFrozen)
                if (blinking && hardcore)
                    HEART_FROZEN_HARDCORE_BLINKING
                else if (blinking)
                    HEART_FROZEN_BLINKING
                else if (hardcore)
                    HEART_FROZEN_HARDCORE
                else
                    HEART_FROZEN
            else
                null
        }
    }
}