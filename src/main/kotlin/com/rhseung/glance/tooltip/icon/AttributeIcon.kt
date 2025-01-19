package com.rhseung.glance.tooltip.icon

import com.rhseung.glance.ModMain
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.registry.entry.RegistryEntry

class AttributeIcon(private val name: String, variants: Int = 1, index: Int = 0) : Icon(variants, 9, 9, index) {
    override val id = ModMain.id("textures/icon/attribute/$name.png");

    override fun get(index: Int): Icon {
        if (index < 0 || index >= variants)
            throw Error("index=$index is not valid");

        return AttributeIcon(name, variants, index);
    }

    fun toAttribute() = when (this) {
        ARMOR -> EntityAttributes.ARMOR
        ARMOR_TOUGHNESS -> EntityAttributes.ARMOR_TOUGHNESS
        ATTACK_DAMAGE -> EntityAttributes.ATTACK_DAMAGE
        ATTACK_KNOCKBACK -> EntityAttributes.ATTACK_KNOCKBACK
        ATTACK_SPEED -> EntityAttributes.ATTACK_SPEED
        BURNING_TIME -> EntityAttributes.BURNING_TIME
        EXPLOSION_KNOCKBACK_RESISTANCE -> EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE
        KNOCKBACK_RESISTANCE -> EntityAttributes.KNOCKBACK_RESISTANCE
        LUCK -> EntityAttributes.LUCK
        MAX_HEALTH -> EntityAttributes.MAX_HEALTH
        MINING_EFFICIENCY -> EntityAttributes.MINING_EFFICIENCY
        MOVEMENT_SPEED -> EntityAttributes.MOVEMENT_SPEED
        OXYGEN_BONUS -> EntityAttributes.OXYGEN_BONUS
        SAFE_FALL_DISTANCE -> EntityAttributes.SAFE_FALL_DISTANCE
        SNEAKING_SPEED -> EntityAttributes.SNEAKING_SPEED
        STEP_HEIGHT -> EntityAttributes.STEP_HEIGHT
        SUBMERGED_MINING_SPEED -> EntityAttributes.SUBMERGED_MINING_SPEED
        SWEEPING_DAMAGE_RATIO -> EntityAttributes.SWEEPING_DAMAGE_RATIO
        WATER_MOVEMENT_EFFICIENCY -> EntityAttributes.WATER_MOVEMENT_EFFICIENCY
        else -> null
    }

    companion object {
        val ARMOR = AttributeIcon("armor");
        val ARMOR_TOUGHNESS = AttributeIcon("armor_toughness");
        val ATTACK_DAMAGE = AttributeIcon("attack_damage");
        val ATTACK_KNOCKBACK = AttributeIcon("attack_knockback");
        val ATTACK_SPEED = AttributeIcon("attack_speed");
        val BURNING_TIME = AttributeIcon("burning_time");
        val EXPLOSION_KNOCKBACK_RESISTANCE = AttributeIcon("explosion_knockback_resistance");
        val KNOCKBACK_RESISTANCE = AttributeIcon("knockback_resistance");
        val LUCK = AttributeIcon("luck");
        val MAX_HEALTH = AttributeIcon("max_health");
        val MINING_EFFICIENCY = AttributeIcon("mining_efficiency");
        val MOVEMENT_SPEED = AttributeIcon("movement_speed");
        val OXYGEN_BONUS = AttributeIcon("oxygen_bonus");
        val REACH_DISTANCE = AttributeIcon("reach_distance");   // TODO: Add this attribute
        val SAFE_FALL_DISTANCE = AttributeIcon("safe_fall_distance");
        val SNEAKING_SPEED = AttributeIcon("sneaking_speed");
        val STEP_HEIGHT = AttributeIcon("step_height");
        val SUBMERGED_MINING_SPEED = AttributeIcon("submerged_mining_speed");
        val SWEEPING_DAMAGE_RATIO = AttributeIcon("sweeping_damage_ratio");
        val WATER_MOVEMENT_EFFICIENCY = AttributeIcon("water_movement_efficiency");
        val UNKNOWN = AttributeIcon("unknown");

        fun RegistryEntry<EntityAttribute>.toIcon() = when (this) {
            EntityAttributes.ARMOR -> ARMOR
            EntityAttributes.ARMOR_TOUGHNESS -> ARMOR_TOUGHNESS
            EntityAttributes.ATTACK_DAMAGE -> ATTACK_DAMAGE
            EntityAttributes.ATTACK_KNOCKBACK -> ATTACK_KNOCKBACK
            EntityAttributes.ATTACK_SPEED -> ATTACK_SPEED
            EntityAttributes.BURNING_TIME -> BURNING_TIME
            EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE -> EXPLOSION_KNOCKBACK_RESISTANCE
            EntityAttributes.KNOCKBACK_RESISTANCE -> KNOCKBACK_RESISTANCE
            EntityAttributes.LUCK -> LUCK
            EntityAttributes.MAX_HEALTH -> MAX_HEALTH
            EntityAttributes.MINING_EFFICIENCY -> MINING_EFFICIENCY
            EntityAttributes.MOVEMENT_SPEED -> MOVEMENT_SPEED
            EntityAttributes.OXYGEN_BONUS -> OXYGEN_BONUS
            EntityAttributes.SAFE_FALL_DISTANCE -> SAFE_FALL_DISTANCE
            EntityAttributes.SNEAKING_SPEED -> SNEAKING_SPEED
            EntityAttributes.STEP_HEIGHT -> STEP_HEIGHT
            EntityAttributes.SUBMERGED_MINING_SPEED -> SUBMERGED_MINING_SPEED
            EntityAttributes.SWEEPING_DAMAGE_RATIO -> SWEEPING_DAMAGE_RATIO
            EntityAttributes.WATER_MOVEMENT_EFFICIENCY -> WATER_MOVEMENT_EFFICIENCY
            else -> UNKNOWN
        }
    }
}
