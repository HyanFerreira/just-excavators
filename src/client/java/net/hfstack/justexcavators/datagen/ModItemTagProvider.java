package net.hfstack.justexcavators.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import net.hfstack.justexcavators.item.ModItems;
import net.hfstack.justexcavators.registry.ModTags;

public final class ModItemTagProvider extends FabricTagsProvider<Item> {
	public ModItemTagProvider(
			FabricPackOutput output,
			CompletableFuture<HolderLookup.Provider> registryLookupFuture
	) {
		super(output, Registries.ITEM, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		List<ResourceKey<Item>> excavators = ModItems.EXCAVATORS.stream()
				.map(item -> BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow())
				.toList();

		builder(ModTags.EXCAVATORS).addAll(excavators);
		builder(ItemTags.SHOVELS).addAll(excavators);
		builder(ItemTags.MINING_ENCHANTABLE).addAll(excavators);
		builder(ItemTags.MINING_LOOT_ENCHANTABLE).addAll(excavators);
		builder(ItemTags.DURABILITY_ENCHANTABLE).addAll(excavators);
		builder(ItemTags.VANISHING_ENCHANTABLE).addAll(excavators);
	}
}
