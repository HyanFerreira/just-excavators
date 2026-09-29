package net.hfstack.justexcavators.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;

import net.hfstack.justexcavators.item.ModItems;

public final class ModPortugueseLanguageProvider extends FabricLanguageProvider {
	public ModPortugueseLanguageProvider(
			FabricPackOutput output,
			CompletableFuture<HolderLookup.Provider> registryLookupFuture
	) {
		super(output, "pt_br", registryLookupFuture);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder translations) {
		translations.add(ModItems.STONE_EXCAVATOR, "Escavadora de Pedra");
		translations.add(ModItems.COPPER_EXCAVATOR, "Escavadora de Cobre");
		translations.add(ModItems.IRON_EXCAVATOR, "Escavadora de Ferro");
		translations.add(ModItems.GOLDEN_EXCAVATOR, "Escavadora de Ouro");
		translations.add(ModItems.DIAMOND_EXCAVATOR, "Escavadora de Diamante");
		translations.add(ModItems.NETHERITE_EXCAVATOR, "Escavadora de Netherita");

		translations.add(ModItems.DEEP_EXCAVATION_CORE, "Núcleo de Escavação Profunda");
		translations.add(ModItems.WIDE_EXCAVATION_CORE, "Núcleo de Escavação Ampla");
		translations.add(ModItems.ADVANCED_EXCAVATION_CORE, "Núcleo de Escavação Avançada");
		translations.add(ModItems.SILK_CORE, "Núcleo de Seda");
		translations.add("advancement.justexcavators.bigger_shovel.title", "Pá Maior");
		translations.add("advancement.justexcavators.bigger_shovel.description", "Fabrique uma Escavadora de Ferro");
		translations.add("advancement.justexcavators.digging_deeper.title", "Escavando Mais Fundo");
		translations.add("advancement.justexcavators.digging_deeper.description", "Obtenha um Núcleo de Escavação Profunda");
		translations.add("advancement.justexcavators.wide_open.title", "Tudo Aberto");
		translations.add("advancement.justexcavators.wide_open.description", "Obtenha um Núcleo de Escavação Ampla");

		translations.add("itemGroup.justexcavators", "Just Excavators");
		translations.add("excavation_mode.justexcavators.basic", "Básico");
		translations.add("excavation_mode.justexcavators.deep", "Profundo");
		translations.add("excavation_mode.justexcavators.wide", "Amplo");
		translations.add("excavation_mode.justexcavators.advanced", "Avançado");
		translations.add("tooltip.justexcavators.mode", "Modo: %s");
		translations.add("tooltip.justexcavators.area", "Área de Escavação: %sx%sx%s");
		translations.add("tooltip.justexcavators.precision", "Segure Shift para desativar a escavação em área");
	}
}
