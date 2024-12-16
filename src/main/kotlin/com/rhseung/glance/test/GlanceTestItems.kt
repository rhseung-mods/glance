package com.rhseung.glance.test

import com.rhseung.glance.ModMain
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.item.equipment.ArmorMaterial
import net.minecraft.item.equipment.EquipmentModels
import net.minecraft.item.equipment.EquipmentType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.ItemTags
import net.minecraft.sound.SoundEvents

object GlanceTestItems {
    fun init() {}

    fun registerArmor(material: ArmorMaterial, type: EquipmentType, path: String): Item {
        val armorItem = ArmorItem(material, type, Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, ModMain.id(path))));
        Registry.register(Registries.ITEM, ModMain.id(path), armorItem);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register { it.add(armorItem) };
        return armorItem;
    }

    val FULL_28 = ArmorMaterial(37, mapOf(
        EquipmentType.HELMET to 7,
        EquipmentType.CHESTPLATE to 7,
        EquipmentType.LEGGINGS to 7,
        EquipmentType.BOOTS to 7,
        EquipmentType.BODY to 7
    ), 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, ItemTags.REPAIRS_NETHERITE_ARMOR, EquipmentModels.DIAMOND);

    val FULL_28_HELMET = registerArmor(FULL_28, EquipmentType.HELMET, "full_28_helmet");
    val FULL_28_CHESTPLATE = registerArmor(FULL_28, EquipmentType.CHESTPLATE, "full_28_chestplate");
    val FULL_28_LEGGINGS = registerArmor(FULL_28, EquipmentType.LEGGINGS, "full_28_leggings");
    val FULL_28_BOOTS = registerArmor(FULL_28, EquipmentType.BOOTS, "full_28_boots");

    val FULL_48 = ArmorMaterial(37, mapOf(
        EquipmentType.HELMET to 12,
        EquipmentType.CHESTPLATE to 12,
        EquipmentType.LEGGINGS to 12,
        EquipmentType.BOOTS to 12,
        EquipmentType.BODY to 12
    ), 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, ItemTags.REPAIRS_NETHERITE_ARMOR, EquipmentModels.NETHERITE);

    val FULL_48_HELMET = registerArmor(FULL_48, EquipmentType.HELMET, "full_48_helmet");
    val FULL_48_CHESTPLATE = registerArmor(FULL_48, EquipmentType.CHESTPLATE, "full_48_chestplate");
    val FULL_48_LEGGINGS = registerArmor(FULL_48, EquipmentType.LEGGINGS, "full_48_leggings");
    val FULL_48_BOOTS = registerArmor(FULL_48, EquipmentType.BOOTS, "full_48_boots");
}