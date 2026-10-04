package net.hfstack.justexcavators.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.hfstack.justexcavators.JustExcavators;

public final class ModBlocks {
	public static final ResourceKey<Block> ENHANCEMENT_WORKBENCH_KEY = ResourceKey.create(
			Registries.BLOCK,
			JustExcavators.id("enhancement_workbench")
	);
	public static final Block ENHANCEMENT_WORKBENCH = register("enhancement_workbench");

	private ModBlocks() {
	}

	public static void init() {
	}

	private static Block register(String name) {
		ResourceKey<Block> key = ENHANCEMENT_WORKBENCH_KEY;
		Block block = new EnhancementWorkbenchBlock(
				BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)
		);
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}
}
