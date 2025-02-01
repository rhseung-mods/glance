package com.rhseung.glance.datagen

import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.with
import com.rhseung.glance.util.Util.titlecase
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.concurrent.CompletableFuture

class LanguageProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricLanguageProvider(output, registryLookup) {

    object Templates {
        val VALUES = mutableListOf<Template>();

        data class Template(val name: String) {
            val translationKey: String = ModMain.id("template.$name").toTranslationKey();
            private val values = mutableListOf<String>();

            val value: String
                get() = values.joinToString("");

            init { VALUES.add(this); }

            fun getText(vararg args: Any): Text {
                val formatted = Text.translatable(translationKey, *args).string;
                val texts = mutableListOf<MutableText>();

                formatted.split("§r§{").forEachIndexed { i, part ->
                    if (i > 0) {
                        val splited = part.split("}");
                        val color = Color(splited[0]);
                        texts.add(splited.slice(1..<splited.size).joinToString("}") with color);
                    }
                }

                return texts.reduce(MutableText::append);
            }

            fun add(str: String, color: Color): Template {
                values.add("§r§{${color.toString()}}$str");
                return this;
            }
        }
    }

    companion object {
        val BURN_AMOUNT = Templates.Template("burn_amount")
            .add("Burn ", Color.GRAY)
            .add("%s", Color.WHITE)
            .add(" Items", Color.GRAY);
    }

    override fun generateTranslations(registryLookup: RegistryWrapper.WrapperLookup, translationBuilder: TranslationBuilder) {
        TranslatableWord.VALUES.forEach { word ->
            translationBuilder.add(word.translationKey, word.name.titlecase());
        }

        translationBuilder.add(BURN_AMOUNT.translationKey, BURN_AMOUNT.value);
    }
}