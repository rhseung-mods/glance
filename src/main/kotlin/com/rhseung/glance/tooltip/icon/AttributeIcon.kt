package com.rhseung.glance.tooltip.icon

import com.rhseung.glance.ModMain
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.registry.entry.RegistryEntry

class AttributeIcon(
    private val name: String,
    val attribute: RegistryEntry<EntityAttribute>? = null,
    variants: Int = 1,
    index: Int = 0
) : Icon(variants, 9, 9, index) {

    override val id = ModMain.id("textures/icon/attribute/$name.png");

    override fun get(index: Int): Icon {
        if (index < 0 || index >= variants)
            throw Error("index=$index is not valid");

        return AttributeIcon(name, attribute, variants, index);
    }

    init {
        VALUES += this;
    }

    companion object {
        val VALUES = mutableListOf<AttributeIcon>();

        val ARMOR = AttributeIcon("armor", EntityAttributes.ARMOR);
        val ARMOR_TOUGHNESS = AttributeIcon("armor_toughness", EntityAttributes.ARMOR_TOUGHNESS);
        val ATTACK_DAMAGE = AttributeIcon("attack_damage", EntityAttributes.ATTACK_DAMAGE);
        val ATTACK_KNOCKBACK = AttributeIcon("attack_knockback", EntityAttributes.ATTACK_KNOCKBACK);
        val ATTACK_SPEED = AttributeIcon("attack_speed", EntityAttributes.ATTACK_SPEED);
        val BURNING_TIME = AttributeIcon("burning_time", EntityAttributes.BURNING_TIME);
        val EXPLOSION_KNOCKBACK_RESISTANCE = AttributeIcon("explosion_knockback_resistance", EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE);
        val KNOCKBACK_RESISTANCE = AttributeIcon("knockback_resistance", EntityAttributes.KNOCKBACK_RESISTANCE);
        val LUCK = AttributeIcon("luck", EntityAttributes.LUCK);
        val MAX_HEALTH = AttributeIcon("max_health", EntityAttributes.MAX_HEALTH);
        val MINING_EFFICIENCY = AttributeIcon("mining_efficiency", EntityAttributes.MINING_EFFICIENCY);
        val MOVEMENT_SPEED = AttributeIcon("movement_speed", EntityAttributes.MOVEMENT_SPEED);
        val OXYGEN_BONUS = AttributeIcon("oxygen_bonus", EntityAttributes.OXYGEN_BONUS);
        val SAFE_FALL_DISTANCE = AttributeIcon("safe_fall_distance", EntityAttributes.SAFE_FALL_DISTANCE);
        val SNEAKING_SPEED = AttributeIcon("sneaking_speed", EntityAttributes.SNEAKING_SPEED);
        val STEP_HEIGHT = AttributeIcon("step_height", EntityAttributes.STEP_HEIGHT);
        val SUBMERGED_MINING_SPEED = AttributeIcon("submerged_mining_speed", EntityAttributes.SUBMERGED_MINING_SPEED);
        val SWEEPING_DAMAGE_RATIO = AttributeIcon("sweeping_damage_ratio", EntityAttributes.SWEEPING_DAMAGE_RATIO);
        val WATER_MOVEMENT_EFFICIENCY = AttributeIcon("water_movement_efficiency", EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
        val UNKNOWN = AttributeIcon("unknown", null);

        fun RegistryEntry<EntityAttribute>.toIcon(): AttributeIcon {
            return VALUES.find { it.attribute == this } ?: UNKNOWN;
        }
    }
}
