package net.hfstack.justexcavators.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

import net.hfstack.justexcavators.JustExcavators;

public final class ModRecipeSerializers {
	public static final RecipeSerializer<SilkCoreSmithingRecipe> SILK_CORE_SMITHING = Registry.register(
			BuiltInRegistries.RECIPE_SERIALIZER,
			JustExcavators.id("silk_core_smithing"),
			SilkCoreSmithingRecipe.SERIALIZER
	);

	private ModRecipeSerializers() {
	}

	public static void init() {
		// Loading this class performs the serializer registration.
	}
}
