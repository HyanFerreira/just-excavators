package net.hfstack.justexcavators.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import net.hfstack.justexcavators.registry.ModTags;
import net.hfstack.justexcavators.block.ModBlocks;

public final class ModBlockTagProvider extends FabricTagsProvider<Block> {
	public ModBlockTagProvider(
			FabricPackOutput output,
			CompletableFuture<HolderLookup.Provider> registryLookupFuture
	) {
		super(output, Registries.BLOCK, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		builder(ModTags.EXCAVATOR_NO_AOE);
		builder(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.ENHANCEMENT_WORKBENCH_KEY);
	}
}
