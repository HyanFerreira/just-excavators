package net.hfstack.justexcavators.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;

import net.hfstack.justexcavators.item.ModItems;

public final class ModEnglishLanguageProvider extends FabricLanguageProvider {
	public ModEnglishLanguageProvider(
			FabricPackOutput output,
			CompletableFuture<HolderLookup.Provider> registryLookupFuture
	) {
		super(output, registryLookupFuture);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder translations) {
		translations.add(ModItems.IRON_EXCAVATOR, "Iron Excavator");
		translations.add(ModItems.GOLDEN_EXCAVATOR, "Golden Excavator");
		translations.add(ModItems.DIAMOND_EXCAVATOR, "Diamond Excavator");
		translations.add(ModItems.NETHERITE_EXCAVATOR, "Netherite Excavator");

		translations.add(ModItems.EXCAVATION_CORE, "Excavation Core");
		translations.add(ModItems.DEEP_EXCAVATION_CORE, "Deep Excavation Core");
		translations.add(ModItems.WIDE_EXCAVATION_CORE, "Wide Excavation Core");
		translations.add(ModItems.SILK_CORE, "Silk Core");

		translations.add("itemGroup.justexcavators", "Just Excavators");
		translations.add("excavation_mode.justexcavators.basic", "Basic");
		translations.add("excavation_mode.justexcavators.deep", "Deep");
		translations.add("excavation_mode.justexcavators.wide", "Wide");
		translations.add("tooltip.justexcavators.mode", "Mode: %s");
		translations.add("tooltip.justexcavators.area", "Excavation Area: %sx%sx%s");
		translations.add("tooltip.justexcavators.precision", "Hold Shift to disable area mining");
	}
}
