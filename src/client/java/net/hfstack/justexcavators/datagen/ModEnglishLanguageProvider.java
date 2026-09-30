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
		translations.add(ModItems.COLLECTOR_CORE, "Collector Core");
		translations.add(ModItems.SMELTING_CORE, "Smelting Core");
		translations.add(ModItems.FILTER_CORE, "Filter Core");
		translations.add(ModItems.VOID_CORE, "Void Core");
		translations.add(ModItems.ENHANCEMENT_WORKBENCH, "Enhancement Workbench");
		translations.add("container.justexcavators.enhancement_workbench", "Enhancement Workbench");

		translations.add("itemGroup.justexcavators", "Just Excavators");
		translations.add("excavation_mode.justexcavators.basic", "Basic");
		translations.add("excavation_mode.justexcavators.deep", "Deep");
		translations.add("excavation_mode.justexcavators.wide", "Wide");
		translations.add("excavation_mode.justexcavators.advanced", "Advanced");
		translations.add("tooltip.justexcavators.mode", "Mode: %s");
		translations.add("tooltip.justexcavators.area", "Excavation Area: %sx%sx%s");
		translations.add("tooltip.justexcavators.enhancement.silk_touch", "Enhancement: Silk Touch");
		translations.add("tooltip.justexcavators.precision", "Hold Shift to disable area mining");
		translations.add("advancement.justexcavators.bigger_shovel.title", "Bigger Shovel");
		translations.add("advancement.justexcavators.bigger_shovel.description", "Obtain your first Excavator");
		translations.add("advancement.justexcavators.digging_deeper.title", "Digging Deeper");
		translations.add("advancement.justexcavators.digging_deeper.description", "Obtain a Deep Excavation Core");
		translations.add("advancement.justexcavators.into_the_depths.title", "Into the Depths");
		translations.add("advancement.justexcavators.into_the_depths.description", "Put a Deep Core to work in an Excavator");
		translations.add("advancement.justexcavators.wide_open.title", "Wide Open");
		translations.add("advancement.justexcavators.wide_open.description", "Obtain a Wide Excavation Core");
		translations.add("advancement.justexcavators.clear_the_way.title", "Clear the Way");
		translations.add("advancement.justexcavators.clear_the_way.description", "Build an Excavator made to clear entire surfaces");
		translations.add("advancement.justexcavators.advanced_engineering.title", "Three Dimensions Ahead");
		translations.add("advancement.justexcavators.advanced_engineering.description", "Obtain an Advanced Excavation Core");
		translations.add("advancement.justexcavators.earthmover.title", "Earthmover");
		translations.add("advancement.justexcavators.earthmover.description", "Command a 5x5x3 Advanced Excavator");
		translations.add("advancement.justexcavators.handle_with_care.title", "Handle With Care");
		translations.add("advancement.justexcavators.handle_with_care.description", "Obtain a Silk Core");
		translations.add("advancement.justexcavators.silken_touch.title", "Leave No Trace");
		translations.add("advancement.justexcavators.silken_touch.description", "Enhance an Excavator with Silk Touch");
		translations.add("advancement.justexcavators.master_of_the_earth.title", "Master of the Earth");
		translations.add("advancement.justexcavators.master_of_the_earth.description", "Collect Basic, Deep, Wide, and Advanced Netherite Excavators");
	}
}
