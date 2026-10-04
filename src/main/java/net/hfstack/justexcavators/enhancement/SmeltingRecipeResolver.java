package net.hfstack.justexcavators.enhancement;

import java.util.Optional;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeType;

public final class SmeltingRecipeResolver {
	private SmeltingRecipeResolver() {
	}

	public static Optional<ItemStack> smeltOne(ServerLevel level, ItemStack input) {
		if (input.isEmpty()) {
			return Optional.empty();
		}
		SimpleContainer recipeInput = new SimpleContainer(input.copyWithCount(1));
		return level.getRecipeManager()
				.getRecipeFor(RecipeType.SMELTING, recipeInput, level)
				.map(recipe -> recipe.assemble(recipeInput, level.registryAccess()))
				.filter(result -> !result.isEmpty());
	}
}
