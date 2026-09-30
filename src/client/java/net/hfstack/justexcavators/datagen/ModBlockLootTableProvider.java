package net.hfstack.justexcavators.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;

import net.minecraft.core.HolderLookup;

import net.hfstack.justexcavators.block.ModBlocks;

public final class ModBlockLootTableProvider extends FabricBlockLootSubProvider {
	public ModBlockLootTableProvider(
			FabricPackOutput output,
			CompletableFuture<HolderLookup.Provider> registriesFuture
	) {
		super(output, registriesFuture);
	}

	@Override
	public void generate() {
		dropSelf(ModBlocks.ENHANCEMENT_WORKBENCH);
	}
}
