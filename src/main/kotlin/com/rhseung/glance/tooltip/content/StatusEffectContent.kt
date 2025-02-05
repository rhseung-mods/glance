package com.rhseung.glance.tooltip.content

import com.rhseung.glance.tooltip.component.LineComponent
import com.rhseung.glance.tooltip.component.TextComponent
import com.rhseung.glance.util.Util.ifElse
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffectUtil
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.consume.ApplyEffectsConsumeEffect
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.collections.plusAssign

class StatusEffectContent(item: Item, itemStack: ItemStack) : GlanceTooltipContent(item, itemStack) {
    override fun getComponents(): List<LineComponent> {
        val tickRate: Float = Item.TooltipContext.create(MinecraftClient.getInstance().world).updateTickRate;
        val effects: List<ApplyEffectsConsumeEffect> = getEffects(itemStack);
        val effectTexts: MutableList<TextComponent> = getEffectTexts(effects, tickRate).toMutableList();

        val noneText = Text.translatable("effect.none").formatted(Formatting.GRAY);
        if (effectTexts.isEmpty())
            effectTexts += TextComponent(noneText);

        return effectTexts.map { LineComponent(it) };
    }

    override fun getShiftComponents(): List<LineComponent> {
        return this.getComponents();
    }

    companion object : Factory {
        fun getEffects(itemStack: ItemStack): List<ApplyEffectsConsumeEffect> {
            val ret = mutableListOf<ApplyEffectsConsumeEffect>();

            if (itemStack.contains(DataComponentTypes.CONSUMABLE)) {
                val consumableComponent = itemStack.get(DataComponentTypes.CONSUMABLE)!!;
                val effects: List<ApplyEffectsConsumeEffect> = consumableComponent.onConsumeEffects().filterIsInstance<ApplyEffectsConsumeEffect>();

                ret += effects;
            }

            if (itemStack.contains(DataComponentTypes.POTION_CONTENTS)) {
                val potionContentsComponent = itemStack.get(DataComponentTypes.POTION_CONTENTS)!!;
                val effects: Iterable<StatusEffectInstance> = potionContentsComponent.effects;

                ret += effects.map(::ApplyEffectsConsumeEffect);
            }

            if (itemStack.contains(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)) {
                val suspiciousStewEffectsComponent = itemStack.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)!!;
                val effects: Iterable<StatusEffectInstance> = suspiciousStewEffectsComponent.effects.map { it.createStatusEffectInstance() };

                ret += effects.map(::ApplyEffectsConsumeEffect);
            }

            if (itemStack.contains(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)) {
                val ominousBottleAmplifierComponent = itemStack.get(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)!!;
                val effects = listOf(StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, ominousBottleAmplifierComponent.value, false, false, true));

                ret += effects.map(::ApplyEffectsConsumeEffect);
            }

            return ret;
        }

        fun getEffectTexts(consumeEffects: Iterable<ApplyEffectsConsumeEffect>, tickRate: Float): List<TextComponent> {
            val effectTexts = mutableListOf<TextComponent>();

            for (consumeEffect: ApplyEffectsConsumeEffect in consumeEffects) {
                val effects: List<StatusEffectInstance> = consumeEffect.effects();
                val probability: Float = consumeEffect.probability;
                val probabilityText: MutableText? = Text.literal("${(probability * 100).toInt()}%")
                    .takeIf { probability < 1f };

                for (effect: StatusEffectInstance in effects) {
                    val effectNameText = Text.translatable(effect.translationKey);
                    val levelText: MutableText? = Text.translatable("potion.potency.${effect.amplifier}")
                        .takeIf { effect.amplifier > 0 };
                    val durationText: MutableText? = (StatusEffectUtil.getDurationText(effect, 1f, tickRate) as MutableText)
                        .takeIf { !effect.isDurationBelow(20) };

                    var effectText = Text.translatable("potion.withAmplifier", effectNameText, levelText)
                        .takeIf { levelText != null } ?: effectNameText;

                    var detailText: MutableText? =
                        if (durationText != null && probabilityText != null)
                            durationText.append(", ").append(probabilityText);
                        else if (durationText != null)
                            durationText;
                        else if (probabilityText != null)
                            probabilityText;
                        else
                            null;

                    if (detailText != null)
                        effectText = Text.translatable("potion.withDuration", effectText, detailText);

                    val formatting: Formatting = effect.effectType.value().category.formatting;
                    effectTexts.add(TextComponent(effectText.formatted(formatting)));
                }
            }

            return effectTexts;
        }

        override fun register() {
            TooltipContentRegistry.register(::StatusEffectContent, ::valid);
        }

        override fun valid(item: Item, itemStack: ItemStack): Boolean {
            return (itemStack.contains(DataComponentTypes.CONSUMABLE) &&
                    itemStack.get(DataComponentTypes.CONSUMABLE)!!.onConsumeEffects.filterIsInstance<ApplyEffectsConsumeEffect>().isNotEmpty()) ||
                    itemStack.contains(DataComponentTypes.POTION_CONTENTS) ||
                    itemStack.contains(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS) ||
                    itemStack.contains(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER);
        }
    }
}