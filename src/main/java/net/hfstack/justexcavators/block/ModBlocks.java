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
	public static final Block ENHANCEMENT_WORKBENCH = register("enhancement_workbench");

	private ModBlocks() {
	}

	public static void init() {
	}

	private static Block register(String name) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, JustExcavators.id(name));
		Block block = new EnhancementWorkbenchBlock(
				BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).setId(key)
		);
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}
}
