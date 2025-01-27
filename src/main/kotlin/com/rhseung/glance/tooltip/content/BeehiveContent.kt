package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.with
import net.minecraft.block.BeehiveBlock
import net.minecraft.block.entity.BeehiveBlockEntity
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.BlockStateComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class BeehiveContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    override fun getComponents(): List<LineComponent> {
        val beeDatas: List<BeehiveBlockEntity.BeeData> = itemStack.get(DataComponentTypes.BEES)!!;
        val blockStateComponent: BlockStateComponent = itemStack
            .getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);

        val bees: Int = beeDatas.size;
        val honeyLevel: Int = blockStateComponent.getValue(BeehiveBlock.HONEY_LEVEL) ?: 0;

        val beesTitle: String = Text.translatable("container.beehive.bees").string.split(':')[0];
        val honeyLevelTitle: String = Text.translatable("container.beehive.honey").string.split(':')[0];

        return listOf(
            LineComponent(TextComponent(
                ("$beesTitle: " with Color.GRAY)
                    .append(bees.toString() with Color.WHITE)
                    .append("/3" with Color.GRAY)
            )),
            LineComponent(TextComponent(
                ("$honeyLevelTitle: " with Color.GRAY)
                    .append(honeyLevel.toString() with Color.WHITE)
                    .append("/5" with Color.GRAY)
            )),
        );
    }

    override fun getShiftComponents(): List<LineComponent> {
        return this.getComponents();
    }

    companion object : Factory {
        override fun register() {
            TooltipContentRegistry.register(::BeehiveContent, ::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return itemStack.contains(DataComponentTypes.BEES);
        }
    }
}