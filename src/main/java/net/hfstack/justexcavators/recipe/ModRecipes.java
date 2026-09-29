package net.hfstack.justexcavators.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

import net.hfstack.justexcavators.JustExcavators;

public final class ModRecipes {
	public static final RecipeSerializer<CoreUpgradeRecipe> CORE_UPGRADE = Registry.register(
			BuiltInRegistries.RECIPE_SERIALIZER,
			JustExcavators.id("core_upgrade"),
			new RecipeSerializer<>(CoreUpgradeRecipe.MAP_CODEC, CoreUpgradeRecipe.STREAM_CODEC)
	);

	private ModRecipes() {
	}

	public static void init() {
		// Loading this class performs the recipe serializer registration.
	}
}
