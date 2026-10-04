package net.hfstack.justexcavators.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

import net.hfstack.justexcavators.JustExcavators;

public final class ModRecipeSerializers {
	public static final RecipeSerializer<ModeShapedRecipe> MODE_SHAPED = Registry.register(
			BuiltInRegistries.RECIPE_SERIALIZER,
			JustExcavators.id("mode_shaped"),
			new ModeShapedRecipe.Serializer()
	);
	private ModRecipeSerializers() {
	}

	public static void init() {
		// Loading this class performs the serializer registration.
	}
}
