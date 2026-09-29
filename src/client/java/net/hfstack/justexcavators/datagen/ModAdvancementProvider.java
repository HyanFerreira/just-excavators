package net.hfstack.justexcavators.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.item.ModItems;

/** Generates the small, standalone gameplay progression for the base profiles. */
public final class ModAdvancementProvider extends FabricAdvancementProvider {
	public ModAdvancementProvider(
			FabricPackOutput output,
			CompletableFuture<HolderLookup.Provider> registryLookupFuture
	) {
		super(output, registryLookupFuture);
	}

	@Override
	public void generateAdvancement(
			HolderLookup.Provider registries,
			Consumer<AdvancementHolder> consumer
	) {
		AdvancementHolder biggerShovel = Advancement.Builder.advancement()
				.rootDisplay(
						ModItems.IRON_EXCAVATOR,
						Component.translatable("advancement.justexcavators.bigger_shovel.title"),
						Component.translatable("advancement.justexcavators.bigger_shovel.description"),
						Identifier.withDefaultNamespace("gui/advancements/backgrounds/stone"),
						AdvancementType.TASK,
						true, true, false
				)
				.addCriterion("has_iron_excavator", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.IRON_EXCAVATOR))
				.save(consumer, JustExcavators.id("bigger_shovel"));

		Advancement.Builder.advancement()
				.parent(biggerShovel)
				.display(
						ModItems.DEEP_EXCAVATION_CORE,
						Component.translatable("advancement.justexcavators.digging_deeper.title"),
						Component.translatable("advancement.justexcavators.digging_deeper.description"),
						AdvancementType.TASK,
						true, true, false
				)
				.addCriterion("has_deep_excavation_core", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DEEP_EXCAVATION_CORE))
				.save(consumer, JustExcavators.id("digging_deeper"));

		Advancement.Builder.advancement()
				.parent(biggerShovel)
				.display(
						ModItems.WIDE_EXCAVATION_CORE,
						Component.translatable("advancement.justexcavators.wide_open.title"),
						Component.translatable("advancement.justexcavators.wide_open.description"),
						AdvancementType.TASK,
						true, true, false
				)
				.addCriterion("has_wide_excavation_core", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.WIDE_EXCAVATION_CORE))
				.save(consumer, JustExcavators.id("wide_open"));
	}
}
