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
		translations.add(ModItems.STONE_EXCAVATOR, "Stone Excavator");
		translations.add(ModItems.COPPER_EXCAVATOR, "Copper Excavator");
		translations.add(ModItems.IRON_EXCAVATOR, "Iron Excavator");
		translations.add(ModItems.GOLDEN_EXCAVATOR, "Golden Excavator");
		translations.add(ModItems.DIAMOND_EXCAVATOR, "Diamond Excavator");
		translations.add(ModItems.NETHERITE_EXCAVATOR, "Netherite Excavator");

		translations.add(ModItems.DEEP_EXCAVATION_CORE, "Deep Excavation Core");
		translations.add(ModItems.WIDE_EXCAVATION_CORE, "Wide Excavation Core");
		translations.add(ModItems.ADVANCED_EXCAVATION_CORE, "Advanced Excavation Core");
		translations.add(ModItems.SILK_CORE, "Silk Core");

		translations.add("itemGroup.justexcavators", "Just Excavators");
		translations.add("excavation_mode.justexcavators.basic", "Basic");
		translations.add("excavation_mode.justexcavators.deep", "Deep");
		translations.add("excavation_mode.justexcavators.wide", "Wide");
		translations.add("excavation_mode.justexcavators.advanced", "Advanced");
		translations.add("tooltip.justexcavators.mode", "Mode: %s");
		translations.add("tooltip.justexcavators.area", "Excavation Area: %sx%sx%s");
		translations.add("tooltip.justexcavators.precision", "Hold Shift to disable area mining");
		translations.add("advancement.justexcavators.bigger_shovel.title", "Bigger Shovel");
		translations.add("advancement.justexcavators.bigger_shovel.description", "Craft an Iron Excavator");
		translations.add("advancement.justexcavators.digging_deeper.title", "Digging Deeper");
		translations.add("advancement.justexcavators.digging_deeper.description", "Obtain a Deep Excavation Core");
		translations.add("advancement.justexcavators.wide_open.title", "Wide Open");
		translations.add("advancement.justexcavators.wide_open.description", "Obtain a Wide Excavation Core");
	}
}
