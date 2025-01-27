package com.rhseung.glance.datagen

import com.rhseung.glance.ModMain
import net.minecraft.text.MutableText
import net.minecraft.text.Text

data class TranslatableWord(val name: String) {
    val translationKey: String = ModMain.id("word.$name").toTranslationKey();

    fun toText(): MutableText {
        return Text.translatable(translationKey);
    }

    init {
        VALUES.add(this);
    }

    companion object {
        val VALUES = mutableListOf<TranslatableWord>();

        val FOOD = TranslatableWord("food");
        val SATURATION = TranslatableWord("saturation");
    }
}