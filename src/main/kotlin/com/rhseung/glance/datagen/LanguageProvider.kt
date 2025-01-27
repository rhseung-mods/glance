package com.rhseung.glance.datagen

import com.rhseung.glance.ModMain
import com.rhseung.glance.util.Util.titlecase
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class LanguageProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricLanguageProvider(output, registryLookup) {

    companion object {
        val BURN_AMOUNT: String = ModMain.id("burn_amount").toTranslationKey();
    }

    override fun generateTranslations(registryLookup: RegistryWrapper.WrapperLookup, translationBuilder: TranslationBuilder) {
        TranslatableWord.VALUES.forEach { word ->
            translationBuilder.add(word.translationKey, word.name.titlecase());
        }

        translationBuilder.add(BURN_AMOUNT, "§7Burn §r§f%s $7Items");
    }
}