package net.hfstack.justexcavators.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.excavation.ExcavationMode;
import net.hfstack.justexcavators.item.ModItems;
import net.hfstack.justexcavators.registry.ModTags;

/** Generates the complete profile and enhancement progression. */
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
						profileIcon(ModItems.STONE_EXCAVATOR, ExcavationMode.BASIC),
						title("bigger_shovel"),
						description("bigger_shovel"),
						Identifier.withDefaultNamespace("block/dirt"),
						AdvancementType.TASK,
						true, true, false
				)
				.addCriterion("has_excavator", InventoryChangeTrigger.TriggerInstance.hasItems(
						excavatorPredicate(registries)
				))
				.save(consumer, JustExcavators.id("bigger_shovel"));

		AdvancementHolder deepCore = coreAdvancement(
				biggerShovel, ModItems.DEEP_EXCAVATION_CORE, "digging_deeper", consumer
		);
		AdvancementHolder deepExcavator = profileAdvancement(
				registries, deepCore, ExcavationMode.DEEP, ModItems.IRON_EXCAVATOR,
				"into_the_depths", consumer
		);
		AdvancementHolder wideCore = coreAdvancement(
				deepExcavator, ModItems.WIDE_EXCAVATION_CORE, "wide_open", consumer
		);
		AdvancementHolder wideExcavator = profileAdvancement(
				registries, wideCore, ExcavationMode.WIDE, ModItems.IRON_EXCAVATOR,
				"clear_the_way", consumer
		);
		AdvancementHolder advancedCore = coreAdvancement(
				wideExcavator, ModItems.ADVANCED_EXCAVATION_CORE, "advanced_engineering", consumer
		);
		AdvancementHolder advancedExcavator = profileAdvancement(
				registries, advancedCore, ExcavationMode.ADVANCED, ModItems.DIAMOND_EXCAVATOR,
				"earthmover", consumer
		);

		AdvancementHolder silkCore = coreAdvancement(
				biggerShovel, ModItems.SILK_CORE, "handle_with_care", consumer
		);
		silkAdvancement(registries, silkCore, consumer);
		netheriteMasteryAdvancement(registries, advancedExcavator, consumer);
	}

	private static AdvancementHolder coreAdvancement(
			AdvancementHolder parent,
			Item core,
			String id,
			Consumer<AdvancementHolder> consumer
	) {
		return Advancement.Builder.advancement()
				.parent(parent)
				.display(core, title(id), description(id), AdvancementType.TASK, true, true, false)
				.addCriterion("has_core", InventoryChangeTrigger.TriggerInstance.hasItems(core))
				.save(consumer, JustExcavators.id(id));
	}

	private static AdvancementHolder profileAdvancement(
			HolderLookup.Provider registries,
			AdvancementHolder parent,
			ExcavationMode mode,
			Item icon,
			String id,
			Consumer<AdvancementHolder> consumer
	) {
		return Advancement.Builder.advancement()
				.parent(parent)
				.display(profileIcon(icon, mode), title(id), description(id), AdvancementType.GOAL, true, true, false)
				.addCriterion("has_profile", InventoryChangeTrigger.TriggerInstance.hasItems(
						excavatorWithModePredicate(registries, mode)
				))
				.save(consumer, JustExcavators.id(id));
	}

	private static void silkAdvancement(
			HolderLookup.Provider registries,
			AdvancementHolder parent,
			Consumer<AdvancementHolder> consumer
	) {
		ItemPredicate.Builder silkExcavator = excavatorPredicate(registries)
				.withComponents(exactComponent(
						ExcavatorComponents.ENHANCEMENTS,
						new ExcavatorEnhancements(true)
				));
		Advancement.Builder.advancement()
				.parent(parent)
				.display(
						ModItems.DIAMOND_EXCAVATOR,
						title("silken_touch"),
						description("silken_touch"),
						AdvancementType.GOAL,
						true, true, false
				)
				.addCriterion("has_silk_excavator", InventoryChangeTrigger.TriggerInstance.hasItems(silkExcavator))
				.save(consumer, JustExcavators.id("silken_touch"));
	}

	private static void netheriteMasteryAdvancement(
			HolderLookup.Provider registries,
			AdvancementHolder parent,
			Consumer<AdvancementHolder> consumer
	) {
		Advancement.Builder builder = Advancement.Builder.advancement()
				.parent(parent)
				.display(
						profileIcon(ModItems.NETHERITE_EXCAVATOR, ExcavationMode.ADVANCED),
						title("master_of_the_earth"),
						description("master_of_the_earth"),
						AdvancementType.CHALLENGE,
						true, true, false
				)
				.requirements(AdvancementRequirements.Strategy.AND);

		for (ExcavationMode mode : ExcavationMode.values()) {
			ItemPredicate.Builder predicate = ItemPredicate.Builder.item()
					.of(registries.lookupOrThrow(Registries.ITEM), ModItems.NETHERITE_EXCAVATOR)
					.withComponents(exactComponent(ExcavatorComponents.EXCAVATION_MODE, mode));
			builder.addCriterion(
					"has_" + mode.getSerializedName(),
					InventoryChangeTrigger.TriggerInstance.hasItems(predicate)
			);
		}

		builder.save(consumer, JustExcavators.id("master_of_the_earth"));
	}

	private static ItemPredicate.Builder excavatorPredicate(HolderLookup.Provider registries) {
		return ItemPredicate.Builder.item().of(registries.lookupOrThrow(Registries.ITEM), ModTags.EXCAVATORS);
	}

	private static ItemPredicate.Builder excavatorWithModePredicate(
			HolderLookup.Provider registries,
			ExcavationMode mode
	) {
		return excavatorPredicate(registries)
				.withComponents(exactComponent(ExcavatorComponents.EXCAVATION_MODE, mode));
	}

	private static <T> DataComponentMatchers exactComponent(DataComponentType<T> type, T value) {
		return DataComponentMatchers.Builder.components()
				.exact(DataComponentExactPredicate.expect(type, value))
				.build();
	}

	private static ItemStackTemplate profileIcon(Item item, ExcavationMode mode) {
		return new ItemStackTemplate(item, DataComponentPatch.builder()
				.set(ExcavatorComponents.EXCAVATION_MODE, mode)
				.build());
	}

	private static Component title(String id) {
		return Component.translatable("advancement.justexcavators." + id + ".title");
	}

	private static Component description(String id) {
		return Component.translatable("advancement.justexcavators." + id + ".description");
	}
}
