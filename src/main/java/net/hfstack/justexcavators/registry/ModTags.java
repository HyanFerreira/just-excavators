package net.hfstack.justexcavators.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;

import net.hfstack.justexcavators.JustExcavators;

public final class ModTags {
	public static final TagKey<Item> EXCAVATORS = TagKey.create(
			Registries.ITEM,
			JustExcavators.id("excavators")
	);
	public static final TagKey<Block> EXCAVATOR_NO_AOE = TagKey.create(
			Registries.BLOCK,
			JustExcavators.id("excavator_no_aoe")
	);

	private ModTags() {
	}
}
