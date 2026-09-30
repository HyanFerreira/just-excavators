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
		translations.add(ModItems.CORE_HOUSING, "Estrutura de Núcleo");
		translations.add(ModItems.SILK_CORE, "Núcleo de Seda");
		translations.add(ModItems.COLLECTOR_CORE, "Núcleo Coletor");
		translations.add(ModItems.SMELTING_CORE, "Núcleo de Fundição");
		translations.add(ModItems.FILTER_CORE, "Núcleo de Filtro");
		translations.add(ModItems.VOID_CORE, "Núcleo do Vazio");
		translations.add(ModItems.ENHANCEMENT_WORKBENCH, "Bancada de Aprimoramento");
		translations.add("container.justexcavators.enhancement_workbench", "Bancada de Aprimoramento");
		translations.add("tag.item.justexcavators.excavators", "Escavadoras");
		translations.add("advancement.justexcavators.bigger_shovel.title", "Pá Maior");
		translations.add("advancement.justexcavators.bigger_shovel.description", "Obtenha sua primeira Escavadora");
		translations.add("advancement.justexcavators.digging_deeper.title", "Escavando Mais Fundo");
		translations.add("advancement.justexcavators.digging_deeper.description", "Obtenha um Núcleo de Escavação Profunda");
		translations.add("advancement.justexcavators.into_the_depths.title", "Rumo às Profundezas");
		translations.add("advancement.justexcavators.into_the_depths.description", "Coloque um Núcleo Profundo para trabalhar em uma Escavadora");
		translations.add("advancement.justexcavators.wide_open.title", "Tudo Aberto");
		translations.add("advancement.justexcavators.wide_open.description", "Obtenha um Núcleo de Escavação Ampla");
		translations.add("advancement.justexcavators.clear_the_way.title", "Abra Caminho");
		translations.add("advancement.justexcavators.clear_the_way.description", "Construa uma Escavadora feita para limpar superfícies inteiras");
		translations.add("advancement.justexcavators.advanced_engineering.title", "Três Dimensões à Frente");
		translations.add("advancement.justexcavators.advanced_engineering.description", "Obtenha um Núcleo de Escavação Avançada");
		translations.add("advancement.justexcavators.earthmover.title", "Move-Terras");
		translations.add("advancement.justexcavators.earthmover.description", "Domine uma Escavadora Avançada 5x5x3");
		translations.add("advancement.justexcavators.handle_with_care.title", "Manuseie com Cuidado");
		translations.add("advancement.justexcavators.handle_with_care.description", "Obtenha um Núcleo de Seda");
		translations.add("advancement.justexcavators.silken_touch.title", "Sem Deixar Rastros");
		translations.add("advancement.justexcavators.silken_touch.description", "Aprimore uma Escavadora com Toque Suave");
		translations.add("advancement.justexcavators.fine_tuning.title", "Ajuste Fino");
		translations.add("advancement.justexcavators.fine_tuning.description", "Obtenha uma Bancada de Aprimoramento");
		translations.add("advancement.justexcavators.power_needs_a_home.title", "Todo Poder Precisa de um Lar");
		translations.add("advancement.justexcavators.power_needs_a_home.description", "Fabrique uma Estrutura de Núcleo");
		translations.add("advancement.justexcavators.nothing_left_behind.title", "Nada Fica para Trás");
		translations.add("advancement.justexcavators.nothing_left_behind.description", "Obtenha um Núcleo Coletor");
		translations.add("advancement.justexcavators.turn_up_the_heat.title", "Aumente o Calor");
		translations.add("advancement.justexcavators.turn_up_the_heat.description", "Obtenha um Núcleo de Fundição");
		translations.add("advancement.justexcavators.only_what_matters.title", "Só o Que Importa");
		translations.add("advancement.justexcavators.only_what_matters.description", "Obtenha um Núcleo de Filtro");
		translations.add("advancement.justexcavators.into_the_void.title", "Rumo ao Vazio");
		translations.add("advancement.justexcavators.into_the_void.description", "Obtenha um Núcleo do Vazio");
		translations.add("advancement.justexcavators.fully_loaded.title", "Carga Máxima");
		translations.add("advancement.justexcavators.fully_loaded.description", "Instale dois Núcleos de Aprimoramento compatíveis em uma Escavadora");
		translations.add("advancement.justexcavators.core_collection.title", "Coleção Completa");
		translations.add("advancement.justexcavators.core_collection.description", "Tenha os cinco Núcleos de Aprimoramento ao mesmo tempo");
		translations.add("advancement.justexcavators.master_of_the_earth.title", "Mestre da Terra");
		translations.add("advancement.justexcavators.master_of_the_earth.description", "Colecione Escavadoras de Netherita Básica, Profunda, Ampla e Avançada");

		translations.add("itemGroup.justexcavators", "Just Excavators");
		translations.add("excavation_mode.justexcavators.basic", "Básico");
		translations.add("excavation_mode.justexcavators.deep", "Profundo");
		translations.add("excavation_mode.justexcavators.wide", "Amplo");
		translations.add("excavation_mode.justexcavators.advanced", "Avançado");
		translations.add("tooltip.justexcavators.mode", "Modo: %s");
		translations.add("tooltip.justexcavators.area", "Área de Escavação: %sx%sx%s");
		translations.add("tooltip.justexcavators.enhancements", "Núcleos de Aprimoramento (%s/%s):");
		translations.add("tooltip.justexcavators.enhancement_slot", "- Slot %s: %s");
		translations.add("tooltip.justexcavators.precision", "Segure Shift para desativar a escavação em área");
		translations.add("enhancement.justexcavators.silk", "Núcleo de Seda");
		translations.add("enhancement.justexcavators.silk.description", "Blocos quebrados soltam a si mesmos quando possível.");
		translations.add("enhancement.justexcavators.collector", "Núcleo Coletor");
		translations.add("enhancement.justexcavators.collector.description", "Os drops são enviados diretamente ao seu inventário.");
		translations.add("enhancement.justexcavators.smelting", "Núcleo de Fundição");
		translations.add("enhancement.justexcavators.smelting.description", "Os drops são processados usando receitas de fornalha.");
		translations.add("enhancement.justexcavators.filter", "Núcleo de Filtro");
		translations.add("enhancement.justexcavators.filter.description", "Apenas blocos correspondentes são escavados na área.");
		translations.add("enhancement.justexcavators.void", "Núcleo do Vazio");
		translations.add("enhancement.justexcavators.void.description", "Blocos destruídos não geram itens ou experiência.");
		translations.add("enhancement.justexcavators.void.warning", "Aviso: os drops descartados não podem ser recuperados.");
	}
}
