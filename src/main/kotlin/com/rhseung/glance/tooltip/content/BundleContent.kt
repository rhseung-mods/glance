package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.component.ContainerComponent
import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.util.Color
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

class BundleContent(item: Item, itemStack: ItemStack) : FloatingTooltipContent(item, itemStack) {
    override fun getComponents(): List<LineComponent> {
        val bundleContent = itemStack.get(DataComponentTypes.BUNDLE_CONTENTS)!!;
        val customName: Text? = itemStack.get(DataComponentTypes.CUSTOM_NAME);
        val color = when (item) {
            Items.BUNDLE -> -1
            Items.WHITE_BUNDLE -> Color(0xE6E6E6).toInt()
            Items.ORANGE_BUNDLE -> Color(0xFB9320).toInt()
            Items.MAGENTA_BUNDLE -> Color(0xCC49B9).toInt()
            Items.LIGHT_BLUE_BUNDLE -> Color(0x30AFE5).toInt()
            Items.YELLOW_BUNDLE -> Color(0xF2C705).toInt()
            Items.LIME_BUNDLE -> Color(0x9BDF39).toInt()
            Items.PINK_BUNDLE -> Color(0xF8A6BD).toInt()
            Items.GRAY_BUNDLE -> Color(0x6C7B83).toInt()
            Items.LIGHT_GRAY_BUNDLE -> Color(0xB1ACA3).toInt()
            Items.CYAN_BUNDLE -> Color(0x14B4B4).toInt()
            Items.PURPLE_BUNDLE -> Color(0x942ACA).toInt()
            Items.BLUE_BUNDLE -> Color(0x4573C7).toInt()
            Items.BROWN_BUNDLE -> Color(0xD18A59).toInt()
            Items.GREEN_BUNDLE -> Color(0x77A119).toInt()
            Items.RED_BUNDLE -> Color(0xD2382E).toInt()
            Items.BLACK_BUNDLE -> Color(0x38364F).toInt()
            else -> -1
        }

        val size = bundleContent.size();
        val maxCol = 9;
        val row = size / maxCol + if (size % maxCol == 0) 0 else 1;

        // todo: size registry, mod support (how?)

        return listOf(LineComponent(
            ContainerComponent(
                bundleContent.stream().toList(),
                row,
                if (row == 1) size else maxCol,
                color,
                bundleContent.selectedStackIndex
            ).let { if (customName != null) it.withTitle(customName) else it }
        ));
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::BundleContent, ::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return itemStack.contains(DataComponentTypes.BUNDLE_CONTENTS) &&
                    !itemStack.get(DataComponentTypes.BUNDLE_CONTENTS)!!.isEmpty;
        }
    }
}