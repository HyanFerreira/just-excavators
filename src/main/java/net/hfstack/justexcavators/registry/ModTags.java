package net.hfstack.justexcavators.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import net.hfstack.justexcavators.JustExcavators;

public final class ModTags {
	public static final TagKey<Item> EXCAVATORS = TagKey.create(
			Registries.ITEM,
			JustExcavators.id("excavators")
	);

	private ModTags() {
	}
}
